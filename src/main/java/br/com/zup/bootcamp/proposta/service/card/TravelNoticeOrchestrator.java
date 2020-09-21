package br.com.zup.bootcamp.proposta.service.card;

import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.zup.bootcamp.proposta.integration.card.CardClientApi;
import br.com.zup.bootcamp.proposta.integration.card.TravelNoticeRequest;
import br.com.zup.bootcamp.proposta.integration.card.TravelNoticeResponse;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.TravelNotice;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import br.com.zup.bootcamp.proposta.repository.TravelNoticeRepository;
import feign.FeignException;

@Service
public class TravelNoticeOrchestrator {

    private final Logger logger = LoggerFactory.getLogger(TravelNoticeOrchestrator.class);

    private final TravelNoticeRepository travelNoticeRepository;
    private final CardRepository cardRepository;
    private final CardClientApi cardClientApi;

    public TravelNoticeOrchestrator(TravelNoticeRepository travelNoticeRepository,
            CardRepository cardRepository, CardClientApi cardClientApi) {
        this.travelNoticeRepository = travelNoticeRepository;
        this.cardRepository = cardRepository;
        this.cardClientApi = cardClientApi;
    }

    public Optional<Card> retrieveCard(@NotNull String cardId) {
        return cardRepository.findById(cardId);
    }

    @Transactional
    public boolean createTravelNotice(@NotNull TravelNotice travelNotice) {
        try {
            final TravelNoticeResponse travelNoticeResponse = cardClientApi.travelNotice(travelNotice.getCard().getId(),
                    TravelNoticeRequest.of(travelNotice));

            if (travelNoticeResponse != null && travelNoticeResponse.isOk()) {
                travelNoticeRepository.save(travelNotice);
                return true;
            }

            return false;
        } catch (FeignException ex) {
            logger.warn(String.format("Travel notice for card %s could not be notified", travelNotice.getCard().getId()), ex);
            return false;
        }
    }
}
