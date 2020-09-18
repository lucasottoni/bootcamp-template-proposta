package br.com.zup.bootcamp.proposta.service.card;

import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.zup.bootcamp.proposta.integration.card.BlockingCardRequest;
import br.com.zup.bootcamp.proposta.integration.card.BlockingCardResponse;
import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardBlock;
import br.com.zup.bootcamp.proposta.model.enumeration.CardStatus;
import br.com.zup.bootcamp.proposta.repository.CardBlockRepository;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import feign.FeignException;

@Service
public class BlockingCardOrchestrator {

    private final Logger logger = LoggerFactory.getLogger(BlockingCardOrchestrator.class);

    private final CardBlockRepository cardBlockRepository;
    private final CardRepository cardRepository;
    private final CardClientApi cardClientApi;
    private final String systemName;

    public BlockingCardOrchestrator(CardBlockRepository cardBlockRepository,
            CardRepository cardRepository, CardClientApi cardClientApi,
            @Value("application.name") String systemName) {
        this.cardBlockRepository = cardBlockRepository;
        this.cardRepository = cardRepository;
        this.cardClientApi = cardClientApi;
        this.systemName = systemName;
    }

    public Optional<Card> retrieveCard(@NotNull String cardId) {
        return cardRepository.findById(cardId);
    }

    @Transactional
    public boolean blockCard(@NotNull CardBlock cardBlock) {
        cardBlockRepository.save(cardBlock);

        try {
            final BlockingCardResponse blockingCardResponse = cardClientApi.blockingCard(cardBlock.getCard().getId(), new BlockingCardRequest(systemName));

            if (blockingCardResponse != null && blockingCardResponse.parseResult() == CardStatus.BLOCKED) {
                cardBlock.getCard().changeStatus(CardStatus.BLOCKED);

                cardRepository.save(cardBlock.getCard());
                return true;
            }
            return false;
        } catch (FeignException ex) {
            logger.warn(String.format("Card %s could not be blocked", cardBlock.getCard().getId()), ex);
            return false;
        }
    }
}
