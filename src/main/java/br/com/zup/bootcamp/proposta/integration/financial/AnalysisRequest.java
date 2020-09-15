package br.com.zup.bootcamp.proposta.integration.financial;

import br.com.zup.bootcamp.proposta.model.Propose;

public class AnalysisRequest {
    private String documento;
    private String nome;
    private String idProposta;

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdProposta() {
        return idProposta;
    }

    public void setIdProposta(String idProposta) {
        this.idProposta = idProposta;
    }

    public static AnalysisRequest fromPropose(Propose propose) {
        AnalysisRequest request = new AnalysisRequest();
        request.idProposta = propose.getId();
        request.documento = propose.getDocument();
        request.nome = propose.getName();
        return request;
    }
}
