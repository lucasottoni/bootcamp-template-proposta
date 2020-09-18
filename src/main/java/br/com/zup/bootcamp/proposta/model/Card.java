package br.com.zup.bootcamp.proposta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;

@Entity
public class Card {

    @Id
    @NotBlank
    private String id;

    @NotBlank
    @OneToOne
    private Propose propose;

    @Column
    @NotBlank
    private String owner;

    protected Card() {
    }

    public Card(@NotBlank String id,
            @NotBlank Propose propose, @NotBlank String owner) {
        this.id = id;
        this.propose = propose;
        this.owner = owner;
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
}
