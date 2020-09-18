package br.com.zup.bootcamp.proposta.service.card;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.zup.bootcamp.proposta.integration.card.BlockingCardRequest;
import br.com.zup.bootcamp.proposta.integration.card.BlockingCardResponse;
import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardBlock;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.model.enumeration.CardStatus;
import br.com.zup.bootcamp.proposta.repository.CardBlockRepository;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import feign.FeignException;

class BlockingCardOrchestratorTest {

    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);
    private final CardBlockRepository cardBlockRepository = Mockito.mock(CardBlockRepository.class);
    private final CardClientApi cardClientApi = Mockito.mock(CardClientApi.class);
    private final String SYSTEM_NAME = "PROPOSTA";

    private final BlockingCardOrchestrator orchestrator = new BlockingCardOrchestrator(
            cardBlockRepository,
            cardRepository,
            cardClientApi,
            SYSTEM_NAME);

    @Test
    @DisplayName("There is a card at retrieve")
    void retrieveCard() {
        String cardId = "CARD-123";

        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        Card card = new Card(cardId, propose, "titular");

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        final Optional<Card> cardResult = orchestrator.retrieveCard(cardId);

        Assertions.assertTrue(cardResult.isPresent());
        Assertions.assertEquals(card, cardResult.get());
    }

    @Test
    @DisplayName("There is no card at retrieve")
    void retrieveNoCard() {
        String cardId = "CARD-123";

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        final Optional<Card> cardResult = orchestrator.retrieveCard(cardId);

        Assertions.assertTrue(cardResult.isEmpty());
    }

    @Test
    @DisplayName("Blocking card success")
    void blockCard() {
        String cardId = "CARD-123";

        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        Card card = new Card(cardId, propose, "titular");

        CardBlock cardBlock = new CardBlock(card, "USER-IP", "USER-AGENT");

        BlockingCardRequest cardBlockRequest = new BlockingCardRequest(SYSTEM_NAME);
        BlockingCardResponse cardBlockResponse = new BlockingCardResponse("BLOQUEADO");

        Mockito.when(cardClientApi.blockingCard(cardId, cardBlockRequest)).thenReturn(cardBlockResponse);

        boolean isBlocked = orchestrator.blockCard(cardBlock);

        Assertions.assertTrue(isBlocked);
        Assertions.assertEquals(CardStatus.BLOCKED, card.getStatus());

        Mockito.verify(cardBlockRepository).save(cardBlock);
        Mockito.verify(cardClientApi).blockingCard(cardId, cardBlockRequest);
        Mockito.verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Could not block card, integration returned other status")
    void couldNotBlock01() {
        String cardId = "CARD-123";

        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        Card card = new Card(cardId, propose, "titular");

        CardBlock cardBlock = new CardBlock(card, "USER-IP", "USER-AGENT");

        BlockingCardRequest cardBlockRequest = new BlockingCardRequest(SYSTEM_NAME);
        BlockingCardResponse cardBlockResponse = new BlockingCardResponse("OUTRO STATUS");

        Mockito.when(cardClientApi.blockingCard(cardId, cardBlockRequest)).thenReturn(cardBlockResponse);

        boolean isBlocked = orchestrator.blockCard(cardBlock);

        Assertions.assertFalse(isBlocked);
        Assertions.assertEquals(CardStatus.GENERATED, card.getStatus());

        Mockito.verify(cardBlockRepository).save(cardBlock);
        Mockito.verify(cardClientApi).blockingCard(cardId, cardBlockRequest);
        Mockito.verify(cardRepository, Mockito.never()).save(card);
    }

    @Test
    @DisplayName("Could not block card, integration failure")
    void couldNotBlock02() {
        String cardId = "CARD-123";

        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        Card card = new Card(cardId, propose, "titular");

        CardBlock cardBlock = new CardBlock(card, "USER-IP", "USER-AGENT");

        BlockingCardRequest cardBlockRequest = new BlockingCardRequest(SYSTEM_NAME);

        Mockito.when(cardClientApi.blockingCard(cardId, cardBlockRequest)).thenThrow(Mockito.mock(FeignException.FeignClientException.class));

        boolean isBlocked = orchestrator.blockCard(cardBlock);

        Assertions.assertFalse(isBlocked);
        Assertions.assertEquals(CardStatus.GENERATED, card.getStatus());

        Mockito.verify(cardBlockRepository).save(cardBlock);
        Mockito.verify(cardClientApi).blockingCard(cardId, cardBlockRequest);
        Mockito.verify(cardRepository, Mockito.never()).save(card);
    }
}
