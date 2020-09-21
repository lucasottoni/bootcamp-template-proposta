package br.com.zup.bootcamp.proposta.model;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class CardBlock {

    @Id
    private String id;

    @NotNull
    @ManyToOne
    private Card card;

    @OneToOne(cascade = CascadeType.PERSIST)
    @NotNull
    private AuditInfo auditInfo;

    protected CardBlock() {
    }

    public CardBlock(@NotNull Card card,
            @NotNull AuditInfo auditInfo) {
        this.id = UUID.randomUUID().toString();
        this.card = card;
        this.auditInfo = auditInfo;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Card getCard() {
        return card;
    }

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }
}
