package br.com.zup.bootcamp.proposta.integration.financial;

import br.com.zup.bootcamp.proposta.model.enumeration.AnalysisStatus;

public class AnalysisResponse {
    private String documento;
    private String nome;
    private String resultadoSolicitacao;
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

    public String getResultadoSolicitacao() {
        return resultadoSolicitacao;
    }

    public void setResultadoSolicitacao(String resultadoSolicitacao) {
        this.resultadoSolicitacao = resultadoSolicitacao;
    }

    public String getIdProposta() {
        return idProposta;
    }

    public void setIdProposta(String idProposta) {
        this.idProposta = idProposta;
    }

    public AnalysisStatus parseStatus() {
        if ("COM_RESTRICAO".equals(this.getResultadoSolicitacao()))
            return AnalysisStatus.NOT_ELIGIBLE;
        if ("SEM_RESTRICAO".equals(this.getResultadoSolicitacao()))
            return AnalysisStatus.ELIGIBLE;
        return AnalysisStatus.NOT_ELIGIBLE;
    }
}
