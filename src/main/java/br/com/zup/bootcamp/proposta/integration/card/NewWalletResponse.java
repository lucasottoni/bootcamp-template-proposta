package br.com.zup.bootcamp.proposta.integration.card;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewWalletResponse {

    @JsonProperty("resultado")
    private final String result;
    @NotBlank
    private final String id;

    public NewWalletResponse(String result, @NotBlank String id) {
        this.result = result;
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public String getId() {
        return id;
    }
}
