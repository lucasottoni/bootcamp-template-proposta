package br.com.zup.bootcamp.proposta.integration.card;

import javax.validation.constraints.NotBlank;

public class GetByProposeRequest {
    private final String idProposta;

    public GetByProposeRequest(@NotBlank String idProposta) {
        this.idProposta = idProposta;
    }

    public String getIdProposta() {
        return idProposta;
    }

}
