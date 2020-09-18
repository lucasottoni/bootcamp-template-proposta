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
import br.com.zup.bootcamp.proposta.repository.CardBlockRepository;
import br.com.zup.bootcamp.proposta.repository.CardRepository;

@RestController
public class BlockingCardController {

    private final CardBlockRepository cardBlockRepository;
    private final CardRepository cardRepository;

    public BlockingCardController(CardBlockRepository cardBlockRepository,
            CardRepository cardRepository) {
        this.cardBlockRepository = cardBlockRepository;
        this.cardRepository = cardRepository;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/card/{cardId}/blocking")
    public void blockCard(@RequestHeader(value = "User-Agent") @NotBlank String userAgent,
            @PathVariable("cardId") String cardId, HttpServletRequest request) {

        String clientIp = getClientIp(request);

        if (clientIp == null || clientIp.length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientIp invalid");
        }

        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "card not found"));

        CardBlock cardBlock = buildCardBlock(card, userAgent, clientIp);
        cardBlockRepository.save(cardBlock);
    }

    private CardBlock buildCardBlock(Card card, String userAgent, String clientIp) {
        return new CardBlock(card, clientIp, userAgent);
    }

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
