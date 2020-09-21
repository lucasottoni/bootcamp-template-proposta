package br.com.zup.bootcamp.proposta.resources;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.model.AuditInfo;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardBlock;
import br.com.zup.bootcamp.proposta.resources.util.ClientIpExtractor;
import br.com.zup.bootcamp.proposta.service.card.BlockingCardOrchestrator;

@RestController
public class BlockingCardController {

    private final BlockingCardOrchestrator blockingCardOrchestrator;
    private final ClientIpExtractor clientIpExtractor;

    public BlockingCardController(BlockingCardOrchestrator blockingCardOrchestrator,
            ClientIpExtractor clientIpExtractor) {
        this.blockingCardOrchestrator = blockingCardOrchestrator;
        this.clientIpExtractor = clientIpExtractor;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/card/{cardId}/blocking")
    public void blockCard(@RequestHeader(value = "User-Agent") @NotBlank String userAgent,
            @PathVariable("cardId") String cardId, HttpServletRequest request) {

        String clientIp = clientIpExtractor.getClientIp(request);

        Card card = blockingCardOrchestrator.retrieveCard(cardId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "card not found"));

        CardBlock cardBlock = buildCardBlock(card, userAgent, clientIp);
        boolean isBlocked = blockingCardOrchestrator.blockCard(cardBlock);
        if (!isBlocked) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "card could not be blocked");
        }
    }

    private CardBlock buildCardBlock(Card card, String userAgent, String clientIp) {
        return new CardBlock(card, new AuditInfo(clientIp, userAgent));
    }

}
