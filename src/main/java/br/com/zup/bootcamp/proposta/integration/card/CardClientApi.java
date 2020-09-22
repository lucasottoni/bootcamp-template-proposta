package br.com.zup.bootcamp.proposta.integration.card;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cardClient", url="${feign.card.url}")
public interface CardClientApi {

    @GetMapping("/cartoes")
    CardResponse getCardByPropose(@SpringQueryMap GetByProposeRequest request);

    @GetMapping("/cartoes/{id}/bloqueios")
    BlockingCardResponse blockingCard(@PathVariable("id") String cardId, @RequestBody BlockingCardRequest request);

    @GetMapping("/cartoes/{id}/avisos")
    TravelNoticeResponse travelNotice(@PathVariable("id") String cardId, @RequestBody TravelNoticeRequest request);

    @GetMapping("/cartoes/{id}/carteiras")
    NewWalletResponse addWallet(@PathVariable("id") String cardId, @RequestBody NewWalletRequest request);
}
