package br.com.zup.bootcamp.proposta.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Biometric {
    @Id
    private String id;

    @ManyToOne
    @NotNull
    private Card card;

    @Column
    @NotBlank
    private String biometry;

    @Column
    private LocalDateTime creationDateTime;

    protected Biometric() {

    }

    public Biometric(@NotNull Card card, @NotBlank String biometry) {
        this.id = UUID.randomUUID().toString();
        this.card = card;
        this.biometry = biometry;
        this.creationDateTime = LocalDateTime.now();
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

    public String getBiometric() {
        return biometry;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }
}
