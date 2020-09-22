package br.com.zup.bootcamp.proposta.service.card;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.zup.bootcamp.proposta.exception.CardNotFoundException;
import br.com.zup.bootcamp.proposta.exception.WalletAlreadyAssociatedException;
import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.integration.card.NewWalletRequest;
import br.com.zup.bootcamp.proposta.integration.card.NewWalletResponse;
import br.com.zup.bootcamp.proposta.integration.card.TravelNoticeRequest;
import br.com.zup.bootcamp.proposta.integration.card.TravelNoticeResponse;
import br.com.zup.bootcamp.proposta.model.AuditInfo;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardWallet;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.model.TravelNotice;
import br.com.zup.bootcamp.proposta.model.enumeration.ExternalWallet;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import br.com.zup.bootcamp.proposta.repository.CardWalletRepository;
import br.com.zup.bootcamp.proposta.repository.TravelNoticeRepository;
import br.com.zup.bootcamp.proposta.resources.in.NewCardWalletRequest;
import feign.FeignException;

class CardWalletOrchestratorTest {

    private final CardWalletRepository cardWalletRepository = Mockito.mock(CardWalletRepository.class);
    private final CardRepository cardRepository = Mockito.mock(CardRepository.class);
    private final CardClientApi cardClientApi = Mockito.mock(CardClientApi.class);

    private final CardWalletOrchestrator orchestrator = new CardWalletOrchestrator(
            cardWalletRepository,
            cardRepository,
            cardClientApi);

    @Test
    @DisplayName("Add wallet")
    void addWallet() {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        Card card = buildCard(cardId);

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(card.getId(), wallet), email);

        NewWalletRequest walletRequest = new NewWalletRequest(email, wallet.name());
        NewWalletResponse walletResponse = new NewWalletResponse("ASSOCIADO", "EXTERNAL-ID");

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        Mockito.when(cardWalletRepository.findById(new CardWallet.Id(cardId, wallet))).thenReturn(Optional.empty());
        Mockito.when(cardClientApi.addWallet(cardId, walletRequest)).thenReturn(walletResponse);

        final Optional<CardWallet> associatedWallet = orchestrator.associateWallet(cardWallet);

        Assertions.assertNotNull(associatedWallet);
        Assertions.assertTrue(associatedWallet.isPresent());
        Assertions.assertEquals("EXTERNAL-ID", associatedWallet.get().getExternalId());

        Mockito.verify(cardWalletRepository).save(cardWallet);
        Mockito.verify(cardClientApi).addWallet(cardId, walletRequest);
    }

    @Test
    @DisplayName("Could not associate wallet, integration failure")
    void couldAssociate() {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        Card card = buildCard(cardId);

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(card.getId(), wallet), email);

        NewWalletRequest walletRequest = new NewWalletRequest(email, wallet.name());

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        Mockito.when(cardClientApi.addWallet(cardId, walletRequest)).thenThrow(Mockito.mock(FeignException.FeignClientException.class));

        final Optional<CardWallet> associateWallet = orchestrator.associateWallet(cardWallet);

        Assertions.assertNotNull(associateWallet);
        Assertions.assertTrue(associateWallet.isEmpty());

        Mockito.verify(cardWalletRepository, Mockito.never()).save(cardWallet);
        Mockito.verify(cardClientApi).addWallet(cardId, walletRequest);
    }

    @Test
    @DisplayName("Wallet already associated")
    void duplicateAssociation() {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        Card card = buildCard(cardId);

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(card.getId(), wallet), email);

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        Mockito.when(cardWalletRepository.findById(new CardWallet.Id(cardId, wallet)))
                .thenReturn(Optional.of(cardWallet));

        WalletAlreadyAssociatedException walletAlreadyAssociatedException =
                Assertions.assertThrows(WalletAlreadyAssociatedException.class,
                        () -> orchestrator.associateWallet(cardWallet));

        Assertions.assertEquals(wallet, walletAlreadyAssociatedException.getExternalWallet());

        Mockito.verify(cardWalletRepository, Mockito.never()).save(cardWallet);
        Mockito.verify(cardClientApi, Mockito.never()).addWallet(Mockito.anyString(), Mockito.any(NewWalletRequest.class));
    }
    @Test
    @DisplayName("Card Not Found")
    void cardNotFound() {
        String cardId = "CARD-123";
        String email = "email@email.com";
        ExternalWallet wallet = ExternalWallet.PAYPAL;

        CardWallet cardWallet = new CardWallet(new CardWallet.Id(cardId, wallet), email);

        CardNotFoundException cardNotFoundException =
                Assertions.assertThrows(CardNotFoundException.class,
                        () -> orchestrator.associateWallet(cardWallet));

        Assertions.assertEquals(cardId, cardNotFoundException.getCardId());

        Mockito.verify(cardWalletRepository, Mockito.never()).save(cardWallet);
        Mockito.verify(cardClientApi, Mockito.never()).addWallet(Mockito.anyString(), Mockito.any(NewWalletRequest.class));
    }

    private Card buildCard(String cardId) {
        Propose propose = new Propose("document", "email@email.com", "nome", "endereco", BigDecimal.ONE);
        return new Card(cardId, propose, "titular");
    }
}
