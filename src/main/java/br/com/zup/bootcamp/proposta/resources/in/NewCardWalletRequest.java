package br.com.zup.bootcamp.proposta.resources.in;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.CardWallet;
import br.com.zup.bootcamp.proposta.model.enumeration.ExternalWallet;

public class NewCardWalletRequest {
    @NotNull
    private final ExternalWallet walletName;

    @NotBlank
    @Email
    private final String email;

    public NewCardWalletRequest(@NotBlank ExternalWallet walletName,
            @NotBlank @Email String email) {
        this.walletName = walletName;
        this.email = email;
    }

    public ExternalWallet getWalletName() {
        return walletName;
    }

    public String getEmail() {
        return email;
    }

    public CardWallet toModel(@NotNull String cardId) {
        return new CardWallet(
                new CardWallet.Id(cardId, this.walletName),
                this.email
        );
    }
}
