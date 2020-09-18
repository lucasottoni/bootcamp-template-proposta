package br.com.zup.bootcamp.proposta.scheduler;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.integration.card.CardResponse;
import br.com.zup.bootcamp.proposta.integration.card.GetByProposeRequest;
import br.com.zup.bootcamp.proposta.model.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.repository.TransactionWrapper;
import feign.FeignException;

class CardGeneratorSchedulerTest {

    private final ProposeRepository proposeRepository = Mockito.mock(ProposeRepository.class);
    private final CardClientApi cardClientApi = Mockito.mock(CardClientApi.class);
    private final TransactionWrapper transactionWrapper = Mockito.mock(TransactionWrapper.class);

    @Test
    @DisplayName("No pending propose")
    void emptyPendingList() {
        CardGeneratorScheduler cardGeneratorScheduler = new CardGeneratorScheduler(
                proposeRepository,
                cardClientApi,
                transactionWrapper);

        Mockito.when(proposeRepository.findPendingCards()).thenReturn(Collections.emptyList());

        cardGeneratorScheduler.generatePendingCards();

        Mockito.verify(cardClientApi, Mockito.never()).getCardByPropose(Mockito.any(GetByProposeRequest.class));
        Mockito.verify(transactionWrapper, Mockito.never()).create(Mockito.any());
        Mockito.verify(transactionWrapper, Mockito.never()).update(Mockito.any());
    }

    @Test
    @DisplayName("Unique pending with card failure")
    void uniquePendingWithoutCardList() {
        CardGeneratorScheduler cardGeneratorScheduler = new CardGeneratorScheduler(
                proposeRepository,
                cardClientApi,
                transactionWrapper);

        Propose propose = new Propose(
                "012345678",
                "email@email.com",
                "nome",
                "endereço",
                BigDecimal.ONE
        );

        Mockito.when(proposeRepository.findPendingCards()).thenReturn(Collections.singletonList(propose));
        Mockito.when(cardClientApi.getCardByPropose(Mockito.any())).thenThrow(
                Mockito.mock(FeignException.FeignClientException.class)
        );

        cardGeneratorScheduler.generatePendingCards();

        Mockito.verify(cardClientApi, Mockito.atMostOnce()).getCardByPropose(Mockito.any(GetByProposeRequest.class));
        Mockito.verify(transactionWrapper, Mockito.never()).create(Mockito.any());
        Mockito.verify(transactionWrapper, Mockito.never()).update(Mockito.any());
    }

    @Test
    @DisplayName("Unique pending with card generated")
    void uniquePendingWithCardList() {
        CardGeneratorScheduler cardGeneratorScheduler = new CardGeneratorScheduler(
                proposeRepository,
                cardClientApi,
                transactionWrapper);

        Propose propose = new Propose(
                "012345678",
                "email@email.com",
                "nome",
                "endereço",
                BigDecimal.ONE
        );
        propose.changeFinancialAnalysis(AnalysisStatus.ELIGIBLE);

        CardResponse cardResponse = new CardResponse();
        cardResponse.setId("CARTAO-1");

        Mockito.when(proposeRepository.findPendingCards()).thenReturn(Collections.singletonList(propose));
        Mockito.when(cardClientApi.getCardByPropose(Mockito.any())).thenReturn(cardResponse);

        cardGeneratorScheduler.generatePendingCards();

        Mockito.verify(cardClientApi, Mockito.atMostOnce()).getCardByPropose(Mockito.any(GetByProposeRequest.class));
        Mockito.verify(transactionWrapper, Mockito.atMostOnce()).create(Mockito.any(Card.class));
    }
}
