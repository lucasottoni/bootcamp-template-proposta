package br.com.zup.bootcamp.proposta.integration.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.zup.bootcamp.proposta.model.enumeration.CardStatus;

public class TravelNoticeResponse {
    @JsonProperty("resultado")
    private final String result;

    @JsonCreator
    public TravelNoticeResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public boolean isOk() {
        return "CRIADO".equalsIgnoreCase(this.result);
    }
}
