package br.com.zup.bootcamp.proposta.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class Biometric {
    @Id
    private String id;

    @Column
    @NotBlank
    private String cardId;

    @Column
    @NotBlank
    private String biometry;

    @Column
    private LocalDateTime creationDateTime;

    protected Biometric() {

    }

    public Biometric(@NotBlank String cardId, @NotBlank String biometry) {
        this.id = UUID.randomUUID().toString();
        this.cardId = cardId;
        this.biometry = biometry;
        this.creationDateTime = LocalDateTime.now();
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCardId() {
        return cardId;
    }

    public String getBiometric() {
        return biometry;
    }

    protected void setCreationDateTime(LocalDateTime localDateTime) {
        this.creationDateTime = localDateTime;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }
}
