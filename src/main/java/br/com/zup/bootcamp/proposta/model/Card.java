package br.com.zup.bootcamp.proposta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.bootcamp.proposta.model.enumeration.CardStatus;

@Entity
public class Card {

    @Id
    @NotBlank
    private String id;

    @NotNull
    @OneToOne
    private Propose propose;

    @Column
    @NotBlank
    private String owner;

    @Column
    @NotNull
    private CardStatus status;

    protected Card() {
    }

    public Card(@NotBlank String id,
            @NotNull Propose propose, @NotBlank String owner) {
        this.id = id;
        this.propose = propose;
        this.owner = owner;
        this.status = CardStatus.GENERATED;
    }

    public String getId() {
        return id;
    }

    public Propose getPropose() {
        return propose;
    }

    public String getOwner() {
        return owner;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void changeStatus(CardStatus cardStatus) {
        this.status = cardStatus;
    }
}
