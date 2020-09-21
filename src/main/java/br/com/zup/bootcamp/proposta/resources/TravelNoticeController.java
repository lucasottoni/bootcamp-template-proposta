package br.com.zup.bootcamp.proposta.resources;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.model.AuditInfo;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.TravelNotice;
import br.com.zup.bootcamp.proposta.resources.in.NewTravelNoticeRequest;
import br.com.zup.bootcamp.proposta.resources.util.ClientIpExtractor;
import br.com.zup.bootcamp.proposta.service.card.TravelNoticeOrchestrator;

@RestController
public class TravelNoticeController {

    private final TravelNoticeOrchestrator travelNoticeOrchestrator;
    private final ClientIpExtractor clientIpExtractor;

    public TravelNoticeController(TravelNoticeOrchestrator travelNoticeOrchestrator, ClientIpExtractor clientIpExtractor) {
        this.travelNoticeOrchestrator = travelNoticeOrchestrator;
        this.clientIpExtractor = clientIpExtractor;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/card/{cardId}/travel-notice")
    public void notifyTravel(@RequestHeader(value = "User-Agent") @NotBlank String userAgent,
            @PathVariable("cardId") String cardId, HttpServletRequest request, @Valid @RequestBody
            NewTravelNoticeRequest payload) {

        String clientIp = clientIpExtractor.getClientIp(request);

        Card card = travelNoticeOrchestrator.retrieveCard(cardId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "card not found"));

        TravelNotice travelNotice = payload.toModel(card, new AuditInfo(clientIp, userAgent));
        final boolean result = travelNoticeOrchestrator.createTravelNotice(travelNotice);
        if (!result) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "card could not be notified for travel");
        }
    }


}
