package br.com.zup.bootcamp.proposta.exception;

import javax.validation.constraints.NotNull;

public class CardNotFoundException extends RuntimeException {
    @NotNull
    private final String cardId;

    public CardNotFoundException(@NotNull String cardId) {
        super("Card " + cardId + " not found");
        this.cardId = cardId;
    }

    public String getCardId() {
        return cardId;
    }
}
