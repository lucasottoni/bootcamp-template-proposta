package br.com.zup.bootcamp.proposta.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.integration.card.CardResponse;
import br.com.zup.bootcamp.proposta.integration.card.GetByProposeRequest;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.repository.TransactionWrapper;
import feign.FeignException;

@Component
@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
public class CardGeneratorScheduler {
    private final Logger logger = LoggerFactory.getLogger(CardGeneratorScheduler.class);

    private final ProposeRepository proposeRepository;
    private final CardClientApi cardClientApi;
    private final TransactionWrapper transactionWrapper;

    public CardGeneratorScheduler(ProposeRepository proposeRepository,
            CardClientApi cardClientApi, TransactionWrapper transactionWrapper) {
        this.proposeRepository = proposeRepository;
        this.cardClientApi = cardClientApi;
        this.transactionWrapper = transactionWrapper;
    }

    @Scheduled(fixedRateString = "${schedule.card.rate}")
    public void generatePendingCards() {
        final List<Propose> pendingCards = proposeRepository.findPendingCards();

        pendingCards.forEach(this::checkCard);
    }

    private void checkCard(Propose propose) {
        GetByProposeRequest request = new GetByProposeRequest(propose.getId());
        try {
            CardResponse response = cardClientApi.getCardByPropose(request);
            Card card = response.toModel(propose);
            propose.setCard(card);
            transactionWrapper.execute(() -> {
                transactionWrapper.create(card);
                transactionWrapper.update(propose);
                return card;
            });
        } catch (FeignException.FeignClientException fce) {
            if (fce.status() != HttpStatus.NOT_FOUND.value()) {
                logger.error("Failure to get pending card info from propose", fce);
            }
        }
    }
}
