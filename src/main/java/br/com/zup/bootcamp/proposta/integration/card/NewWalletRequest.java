package br.com.zup.bootcamp.proposta.integration.card;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.zup.bootcamp.proposta.model.CardWallet;

public class NewWalletRequest {

    @NotBlank
    private final String email;
    @NotBlank
    @JsonProperty("carteira")
    private final String walletName;

    public NewWalletRequest(@NotBlank String email, @NotBlank String walletName) {
        this.email = email;
        this.walletName = walletName;
    }

    public String getEmail() {
        return email;
    }

    public String getWalletName() {
        return walletName;
    }

    public static NewWalletRequest of(CardWallet cardWallet) {
        return new NewWalletRequest(
                cardWallet.getEmail(),
                cardWallet.getId().getWalletName().name()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewWalletRequest that = (NewWalletRequest) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(walletName, that.walletName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, walletName);
    }
}
