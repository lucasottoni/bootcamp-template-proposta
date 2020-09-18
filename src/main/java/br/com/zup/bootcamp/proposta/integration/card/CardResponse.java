package br.com.zup.bootcamp.proposta.integration.card;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;

public class CardResponse {
    private String id;
    private String idProposta;
    private String titular;
    private BigDecimal limite;
    private Due vencimento;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProposta() {
        return idProposta;
    }

    public void setIdProposta(String idProposta) {
        this.idProposta = idProposta;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public Due getVencimento() {
        return vencimento;
    }

    public void setVencimento(Due vencimento) {
        this.vencimento = vencimento;
    }

    public Card toModel(Propose propose) {
        return new Card(
                this.id,
                propose,
                this.titular
        );
    }

    public static class Due {
        private String id;
        private Integer dia;
        private LocalDateTime dataDeCriacao;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getDia() {
            return dia;
        }

        public void setDia(Integer dia) {
            this.dia = dia;
        }

        public LocalDateTime getDataDeCriacao() {
            return dataDeCriacao;
        }

        public void setDataDeCriacao(LocalDateTime dataDeCriacao) {
            this.dataDeCriacao = dataDeCriacao;
        }
    }
}
