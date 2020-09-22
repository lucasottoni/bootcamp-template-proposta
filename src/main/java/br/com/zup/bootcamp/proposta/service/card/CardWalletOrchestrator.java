package br.com.zup.bootcamp.proposta.service.card;

import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.zup.bootcamp.proposta.exception.CardNotFoundException;
import br.com.zup.bootcamp.proposta.exception.WalletAlreadyAssociatedException;
import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.integration.card.NewWalletRequest;
import br.com.zup.bootcamp.proposta.integration.card.NewWalletResponse;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardWallet;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import br.com.zup.bootcamp.proposta.repository.CardWalletRepository;
import feign.FeignException;

@Service
public class CardWalletOrchestrator {
    private final Logger logger = LoggerFactory.getLogger(CardWalletOrchestrator.class);

    private final CardWalletRepository cardWalletRepository;
    private final CardRepository cardRepository;
    private final CardClientApi cardClientApi;

    public CardWalletOrchestrator(CardWalletRepository cardWalletRepository,
            CardRepository cardRepository, CardClientApi cardClientApi) {
        this.cardWalletRepository = cardWalletRepository;
        this.cardRepository = cardRepository;
        this.cardClientApi = cardClientApi;
    }

    @Transactional
    public Optional<CardWallet> associateWallet(CardWallet cardWallet) {
        final Card card = cardRepository.findById(cardWallet.getId().getCardId()).orElseThrow(() ->
                new CardNotFoundException(cardWallet.getId().getCardId())
        );
        final Optional<CardWallet> previous = cardWalletRepository.findById(cardWallet.getId());
        if (previous.isPresent()) {
            throw new WalletAlreadyAssociatedException(card, previous.get().getId().getWalletName());
        }

        final NewWalletResponse newWalletResponse = callIntegration(cardWallet.getId().getCardId(), NewWalletRequest.of(cardWallet));

        if (newWalletResponse != null && newWalletResponse.getId() != null) {
            cardWallet.setExternalId(newWalletResponse.getId());
            cardWalletRepository.save(cardWallet);
            return Optional.of(cardWallet);
        }
        return Optional.empty();
    }

    private NewWalletResponse callIntegration(String cardId, NewWalletRequest request) {
        try {
            return cardClientApi.addWallet(cardId, request);
        } catch (FeignException.FeignClientException fce) {
            logger.warn("Failure to call integration.", fce);
            return null;
        }
    }
}
