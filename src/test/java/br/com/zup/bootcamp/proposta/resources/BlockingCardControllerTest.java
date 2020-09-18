package br.com.zup.bootcamp.proposta.resources;

import java.math.BigDecimal;
import java.util.Optional;

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

import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardBlock;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.service.card.BlockingCardOrchestrator;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = BlockingCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BlockingCardControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BlockingCardOrchestrator blockingCardOrchestrator;

    @Test
    @DisplayName("Blocking card")
    void blockCard() throws Exception {
        String cardId = "CARD-123";
        String userIp = "127.0.0.1";
        String userAgent = "test-agent";

        Card card = new Card(
                cardId,
                new Propose("documento", "email@email.com", "nome", "enderco", BigDecimal.ONE),
                "titular X"
        );

        Mockito.when(blockingCardOrchestrator.retrieveCard(cardId)).thenReturn(Optional.of(card));

        CardBlock cardBlock = new CardBlock(card, userIp, userAgent);
        Mockito.when(blockingCardOrchestrator.blockCard(Mockito.any(CardBlock.class)))
                .then((invocationOnMock) -> answerBlockCard(invocationOnMock, cardBlock, true));

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .header(HttpHeaders.USER_AGENT, userAgent)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    @DisplayName("Card could not be blocked at integration")
    void blockCardFailed() throws Exception {
        String cardId = "CARD-123";
        String userIp = "127.0.0.1";
        String userAgent = "test-agent";

        Card card = new Card(
                cardId,
                new Propose("documento", "email@email.com", "nome", "enderco", BigDecimal.ONE),
                "titular X"
        );

        CardBlock cardBlock = new CardBlock(card, userIp, userAgent);

        Mockito.when(blockingCardOrchestrator.retrieveCard(cardId)).thenReturn(Optional.of(card));
        Mockito.when(blockingCardOrchestrator.blockCard(Mockito.any(CardBlock.class)))
                .then((invocationOnMock) -> answerBlockCard(invocationOnMock, cardBlock, false));

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .header(HttpHeaders.USER_AGENT, userAgent)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFailedDependency());

    }

    @Test
    @DisplayName("Fail to save without user agent")
    void invalidBase64() throws Exception {
        String cardId = "CARD-123";
        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @DisplayName("Fail to save not found card")
    void proposeNotFound() throws Exception {
        String cardId = "CARD-123";

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .header(HttpHeaders.USER_AGENT, "test-agent")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private String getUrl(String cardId) {
        return "/api/card/" + cardId + "/blocking";
    }

    private boolean answerBlockCard(InvocationOnMock invocationOnMock, CardBlock cardBlock, boolean result) {
        CardBlock arg0 = invocationOnMock.getArgument(0);
        if (isSameCardBlockRequest(arg0, cardBlock)) {
            return result;
        } else {
            throw new IllegalArgumentException("Not expected value");
        }
    }

    private boolean isSameCardBlockRequest(CardBlock arg0, CardBlock arg1) {
        return arg0.getCard() == arg1.getCard()
                && arg0.getUserAgent().equalsIgnoreCase(arg1.getUserAgent())
                && arg0.getUserIp().equalsIgnoreCase(arg1.getUserIp());
    }
}
