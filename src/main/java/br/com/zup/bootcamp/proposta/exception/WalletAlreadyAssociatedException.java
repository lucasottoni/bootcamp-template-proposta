package br.com.zup.bootcamp.proposta.exception;

import javax.validation.constraints.NotNull;

import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.enumeration.ExternalWallet;

public class WalletAlreadyAssociatedException extends RuntimeException {
    @NotNull
    private final Card card;
    @NotNull
    private final ExternalWallet externalWallet;

    public WalletAlreadyAssociatedException(
            @NotNull Card card,
            @NotNull ExternalWallet externalWallet) {
        this.card = card;
        this.externalWallet = externalWallet;
    }

    public Card getCard() {
        return card;
    }

    public ExternalWallet getExternalWallet() {
        return externalWallet;
    }
}
