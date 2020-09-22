package br.com.zup.bootcamp.proposta.resources;

import java.math.BigDecimal;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.hamcrest.CoreMatchers;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.zup.bootcamp.proposta.exception.CardNotFoundException;
import br.com.zup.bootcamp.proposta.exception.WalletAlreadyAssociatedException;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardWallet;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.model.enumeration.ExternalWallet;
import br.com.zup.bootcamp.proposta.resources.in.NewCardWalletRequest;
import br.com.zup.bootcamp.proposta.service.card.CardWalletOrchestrator;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AddCardWalletController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddCardWalletControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CardWalletOrchestrator orchestrator;

    @Test
    @DisplayName("add wallet")
    void addWallet() throws Exception {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(cardId, wallet), email);

        CardWallet result = new CardWallet(new CardWallet.Id(cardId, wallet), email);
        result.setExternalId("EXTERNAL-ID");

        Mockito.when(orchestrator.associateWallet(Mockito.any(CardWallet.class)))
                .then((invocationOnMock) -> answerAssociateWallet(invocationOnMock, cardWallet, result));

        NewCardWalletRequest cardWalletRequest = new NewCardWalletRequest(wallet, email);

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(cardWalletRequest))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, CoreMatchers.containsString(result.getExternalId())));

    }

    @Test
    @DisplayName("Could not associate wallet at integration")
    void integrationFailed() throws Exception {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(cardId, wallet), email);
        Mockito.when(orchestrator.associateWallet(Mockito.any(CardWallet.class)))
                .then((invocationOnMock) -> answerAssociateWallet(invocationOnMock, cardWallet, null));

        NewCardWalletRequest request = new NewCardWalletRequest(wallet, email);
        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(request))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFailedDependency());

    }

    @Test
    @DisplayName("Wallet already associated")
    void walletAlreadyAssociated() throws Exception {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        Card card = getCardDefault(cardId);

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(cardId, wallet), email);

        Mockito.when(orchestrator.associateWallet(Mockito.any(CardWallet.class)))
                .then((invocationOnMock) -> failure(invocationOnMock, cardWallet,
                        new WalletAlreadyAssociatedException(card, wallet)));

        NewCardWalletRequest request = new NewCardWalletRequest(wallet, email);

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(request))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

    }

    @Test
    @DisplayName("Fail to save not found card")
    void cardNotFound() throws Exception {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(cardId, wallet), email);

        Mockito.when(orchestrator.associateWallet(Mockito.any(CardWallet.class)))
                .then((invocationOnMock) -> failure(invocationOnMock, cardWallet,
                        new CardNotFoundException(cardId)));

        NewCardWalletRequest request = new NewCardWalletRequest(wallet, email);
        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(request))
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
        return "/api/card/" + cardId + "/wallet";
    }

    private Optional<CardWallet> answerAssociateWallet(InvocationOnMock invocationOnMock, CardWallet cardWallet,
            CardWallet result) {
        CardWallet arg0 = invocationOnMock.getArgument(0);
        return answerAssociateWallet(arg0, cardWallet, result, null);
    }

    private Optional<CardWallet> failure(InvocationOnMock invocationOnMock, CardWallet cardWallet,
            @NotNull RuntimeException exception) {
        CardWallet arg0 = invocationOnMock.getArgument(0);
        return answerAssociateWallet(arg0, cardWallet, null, exception);
    }

    private Optional<CardWallet> answerAssociateWallet(CardWallet arg0, CardWallet cardWallet,
            CardWallet result, RuntimeException exception) {
        if (isSame(arg0, cardWallet)) {
            if (exception != null)
                throw exception;
            return Optional.ofNullable(result);
        } else {
            String arg0Str = asJsonString(arg0);
            String paramStr = asJsonString(cardWallet);
            throw new IllegalArgumentException("Not expected value: \n" + arg0Str + "\n" + paramStr);
        }
    }

    private boolean isSame(CardWallet arg0, CardWallet arg1) {
        return arg0.getId().equals(arg1.getId())
                && arg0.getEmail().equalsIgnoreCase(arg1.getEmail());
    }

    private String asJsonString(NewCardWalletRequest request) throws JsonProcessingException {
        return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(request);
    }

    private String asJsonString(CardWallet cardWallet) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(cardWallet);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
