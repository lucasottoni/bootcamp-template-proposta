package br.com.zup.bootcamp.proposta.integration.card;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cardClient", url="${feign.card.url}")
public interface CardClientApi {

    @GetMapping("/cartoes")
    CardResponse getCardByPropose(@SpringQueryMap GetByProposeRequest request);

}
