package br.com.zup.bootcamp.proposta.integration.card;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.zup.bootcamp.proposta.model.enumeration.CardStatus;

public class BlockingCardResponse {
    @JsonProperty("resultado")
    private final String result;

    @JsonCreator
    public BlockingCardResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public CardStatus parseResult() {
        if ("BLOQUEADO".equalsIgnoreCase(this.result))
            return CardStatus.BLOCKED;
        return null;
    }
}
