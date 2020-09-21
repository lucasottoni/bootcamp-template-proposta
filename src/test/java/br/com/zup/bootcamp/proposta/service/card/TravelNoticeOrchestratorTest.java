package br.com.zup.bootcamp.proposta.service.card;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.integration.card.TravelNoticeRequest;
import br.com.zup.bootcamp.proposta.integration.card.TravelNoticeResponse;
import br.com.zup.bootcamp.proposta.model.AuditInfo;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.model.TravelNotice;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import br.com.zup.bootcamp.proposta.repository.TravelNoticeRepository;
import feign.FeignException;

class TravelNoticeOrchestratorTest {

    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);
    private final TravelNoticeRepository travelNoticeRepository = Mockito.mock(TravelNoticeRepository.class);
    private final CardClientApi cardClientApi = Mockito.mock(CardClientApi.class);

    private final TravelNoticeOrchestrator orchestrator = new TravelNoticeOrchestrator(
            travelNoticeRepository,
            cardRepository,
            cardClientApi);

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
    @DisplayName("Travel notice success")
    void doTravelNotice() {
        String cardId = "CARD-123";
        String destination = "udia";
        LocalDate dueAt = LocalDate.now().plusDays(3);

        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        Card card = new Card(cardId, propose, "titular");

        TravelNotice travelNotice = new TravelNotice(card, destination, dueAt,
                new AuditInfo("USER-IP", "USER-AGENT"));

        TravelNoticeRequest travelNoticeRequest = new TravelNoticeRequest(destination, dueAt);
        TravelNoticeResponse travelNoticeResponse = new TravelNoticeResponse("CRIADO");

        Mockito.when(cardClientApi.travelNotice(cardId, travelNoticeRequest)).thenReturn(travelNoticeResponse);

        boolean isNotified = orchestrator.createTravelNotice(travelNotice);

        Assertions.assertTrue(isNotified);

        Mockito.verify(travelNoticeRepository).save(travelNotice);
        Mockito.verify(cardClientApi).travelNotice(cardId, travelNoticeRequest);
    }

    @Test
    @DisplayName("Could not do travel notice card, integration returned other status")
    void couldNotNotice01() {
        String cardId = "CARD-123";
        String destination = "udia";
        LocalDate dueAt = LocalDate.now().plusDays(3);

        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        Card card = new Card(cardId, propose, "titular");

        TravelNotice travelNotice = new TravelNotice(card, destination, dueAt,
                new AuditInfo("USER-IP", "USER-AGENT"));

        TravelNoticeRequest travelNoticeRequest = new TravelNoticeRequest(destination, dueAt);
        TravelNoticeResponse travelNoticeResponse = new TravelNoticeResponse("OUTRO STATUS");

        Mockito.when(cardClientApi.travelNotice(cardId, travelNoticeRequest)).thenReturn(travelNoticeResponse);

        boolean isNotified = orchestrator.createTravelNotice(travelNotice);

        Assertions.assertFalse(isNotified);

        Mockito.verify(travelNoticeRepository, Mockito.never()).save(travelNotice);
        Mockito.verify(cardClientApi).travelNotice(cardId, travelNoticeRequest);
    }

    @Test
    @DisplayName("Could not notify travel, integration failure")
    void couldNotNotice02() {
        String cardId = "CARD-123";
        String destination = "udia";
        LocalDate dueAt = LocalDate.now().plusDays(3);

        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        Card card = new Card(cardId, propose, "titular");

        TravelNotice travelNotice = new TravelNotice(card, destination, dueAt,
                new AuditInfo("USER-IP", "USER-AGENT"));

        TravelNoticeRequest travelNoticeRequest = new TravelNoticeRequest(destination, dueAt);

        Mockito.when(cardClientApi.travelNotice(cardId, travelNoticeRequest)).thenThrow(Mockito.mock(FeignException.FeignClientException.class));

        boolean isNotified = orchestrator.createTravelNotice(travelNotice);

        Assertions.assertFalse(isNotified);

        Mockito.verify(travelNoticeRepository, Mockito.never()).save(travelNotice);
        Mockito.verify(cardClientApi).travelNotice(cardId, travelNoticeRequest);
        Mockito.verify(cardRepository, Mockito.never()).save(card);
    }

}
