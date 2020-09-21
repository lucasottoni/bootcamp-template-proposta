package br.com.zup.bootcamp.proposta.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.zup.bootcamp.proposta.model.AuditInfo;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardBlock;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.model.TravelNotice;
import br.com.zup.bootcamp.proposta.resources.in.NewBiometricRequest;
import br.com.zup.bootcamp.proposta.resources.in.NewTravelNoticeRequest;
import br.com.zup.bootcamp.proposta.resources.util.ClientIpExtractor;
import br.com.zup.bootcamp.proposta.service.card.BlockingCardOrchestrator;
import br.com.zup.bootcamp.proposta.service.card.TravelNoticeOrchestrator;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = TravelNoticeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TravelNoticeControllerTest {

    private final String userIp = "127.0.0.1";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TravelNoticeOrchestrator orchestrator;

    @MockBean
    private ClientIpExtractor clientIpExtractor;

    @BeforeEach
    void setup() {
        Mockito.when(clientIpExtractor.getClientIp(Mockito.any(HttpServletRequest.class))).thenReturn(userIp);
    }

    @Test
    @DisplayName("Travel notice")
    void doTravelNotice() throws Exception {
        String cardId = "CARD-123";
        String destination = "UDIA";
        LocalDate dueAt = LocalDate.now().plusDays(5);
        String userAgent = "test-agent";

        Card card = getCardDefault(cardId);

        Mockito.when(orchestrator.retrieveCard(cardId)).thenReturn(Optional.of(card));

        TravelNotice travelNotice = new TravelNotice(card, destination, dueAt, new AuditInfo(userIp, userAgent));
        Mockito.when(orchestrator.createTravelNotice(Mockito.any(TravelNotice.class)))
                .then((invocationOnMock) -> answerTravelNotice(invocationOnMock, travelNotice, true));

        NewTravelNoticeRequest travelNoticeRequest = new NewTravelNoticeRequest(destination, dueAt);

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(travelNoticeRequest))
                        .header(HttpHeaders.USER_AGENT, userAgent)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    @DisplayName("Travel could not be noticed  at integration")
    void travelNoticeFailed() throws Exception {
        String cardId = "CARD-123";
        String destination = "UDIA";
        LocalDate dueAt = LocalDate.now().plusDays(5);
        String userAgent = "test-agent";

        Card card = getCardDefault(cardId);

        Mockito.when(orchestrator.retrieveCard(cardId)).thenReturn(Optional.of(card));

        TravelNotice travelNotice = new TravelNotice(card, destination, dueAt, new AuditInfo(userIp, userAgent));
        Mockito.when(orchestrator.createTravelNotice(Mockito.any(TravelNotice.class)))
                .then((invocationOnMock) -> answerTravelNotice(invocationOnMock, travelNotice, false));

        NewTravelNoticeRequest travelNoticeRequest = new NewTravelNoticeRequest(destination, dueAt);
        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(travelNoticeRequest))
                        .header(HttpHeaders.USER_AGENT, userAgent)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFailedDependency());

    }

    @Test
    @DisplayName("Fail to save without user agent")
    void emptyUserAgent() throws Exception {
        String cardId = "CARD-123";
        String destination = "UDIA";
        LocalDate dueAt = LocalDate.now().plusDays(5);
        NewTravelNoticeRequest travelNoticeRequest = new NewTravelNoticeRequest(destination, dueAt);

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(travelNoticeRequest))
                        .content(asJsonString(travelNoticeRequest))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @DisplayName("Fail to save not found card")
    void cardNotFound() throws Exception {
        String cardId = "CARD-123";
        String destination = "UDIA";
        LocalDate dueAt = LocalDate.now().plusDays(5);
        NewTravelNoticeRequest travelNoticeRequest = new NewTravelNoticeRequest(destination, dueAt);

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(travelNoticeRequest))
                        .header(HttpHeaders.USER_AGENT, "test-agent")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private Card getCardDefault(String cardId) {
        return new Card(
                cardId,
                new Propose("documento", "email@email.com", "nome", "enderco", BigDecimal.ONE),
                "titular X"
        );
    }

    private String getUrl(String cardId) {
        return "/api/card/" + cardId + "/travel-notice";
    }

    private boolean answerTravelNotice(InvocationOnMock invocationOnMock, TravelNotice travelNotice, boolean result) {
        TravelNotice arg0 = invocationOnMock.getArgument(0);
        if (isSameTravelNotice(arg0, travelNotice)) {
            return result;
        } else {
            String arg0Str = asJsonString(arg0);
            String paramStr = asJsonString(travelNotice);
            throw new IllegalArgumentException("Not expected value: \n" + arg0Str + "\n" + paramStr );
        }
    }

    private boolean isSameTravelNotice(TravelNotice arg0, TravelNotice arg1) {
        return arg0.getCard() == arg1.getCard()
                && arg0.getTravelDestination().equalsIgnoreCase(arg1.getTravelDestination())
                && arg0.getTravelEnd().isEqual(arg1.getTravelEnd())
                && arg0.getAuditInfo().getUserAgent().equalsIgnoreCase(arg1.getAuditInfo().getUserAgent())
                && arg0.getAuditInfo().getUserIp().equalsIgnoreCase(arg1.getAuditInfo().getUserIp());
    }

    private String asJsonString(NewTravelNoticeRequest request) throws JsonProcessingException {
        return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(request);
    }

    private String asJsonString(TravelNotice travelNotice) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(travelNotice);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
