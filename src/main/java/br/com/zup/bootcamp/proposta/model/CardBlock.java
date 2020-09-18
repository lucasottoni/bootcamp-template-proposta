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
public class CardBlock {

    @Id
    private String id;

    @NotNull
    @ManyToOne
    private Card card;

    @Column
    @NotNull
    private LocalDateTime blockingTime;

    @Column
    @NotBlank
    private String userIp;

    @Column
    @NotBlank
    private String userAgent;

    protected CardBlock() {
    }

    public CardBlock(@NotNull Card card,
            @NotBlank String userIp,
            @NotBlank String userAgent) {
        this.id = UUID.randomUUID().toString();
        this.card = card;
        this.blockingTime = LocalDateTime.now();
        this.userIp = userIp;
        this.userAgent = userAgent;
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

    public LocalDateTime getBlockingTime() {
        return blockingTime;
    }

    public String getUserIp() {
        return userIp;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
