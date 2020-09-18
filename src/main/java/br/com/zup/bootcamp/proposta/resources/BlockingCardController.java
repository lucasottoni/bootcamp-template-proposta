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

import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardBlock;
import br.com.zup.bootcamp.proposta.service.card.BlockingCardOrchestrator;

@RestController
public class BlockingCardController {

    private final BlockingCardOrchestrator blockingCardOrchestrator;

    public BlockingCardController(BlockingCardOrchestrator blockingCardOrchestrator) {
        this.blockingCardOrchestrator = blockingCardOrchestrator;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/card/{cardId}/blocking")
    public void blockCard(@RequestHeader(value = "User-Agent") @NotBlank String userAgent,
            @PathVariable("cardId") String cardId, HttpServletRequest request) {

        String clientIp = getClientIp(request);

        Card card = blockingCardOrchestrator.retrieveCard(cardId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "card not found"));

        CardBlock cardBlock = buildCardBlock(card, userAgent, clientIp);
        boolean isBlocked = blockingCardOrchestrator.blockCard(cardBlock);
        if (!isBlocked) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "card could not be blocked");
        }
    }

    private CardBlock buildCardBlock(Card card, String userAgent, String clientIp) {
        return new CardBlock(card, clientIp, userAgent);
    }

    @NotBlank
    private String getClientIp(HttpServletRequest request) {
        final String[] ipHeaders = new String[]{
                "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        };

        for (String header : ipHeaders) {
            String ip = request.getHeader(header);
            if (isValidIp(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    private boolean isValidIp(String ip) {
        return ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip);
    }
}
