package br.com.zup.bootcamp.proposta.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.bootcamp.proposta.model.enumeration.ExternalWallet;

@Entity
public class CardWallet {

    @EmbeddedId
    @NotNull
    private Id id;

    @NotBlank
    @Email
    @Column
    private String email;

    @Column(unique = true)
    private String externalId;

    protected CardWallet() {
    }

    public CardWallet(@NotNull Id id,
            @NotBlank @Email String email) {
        this.id = id;
        this.email = email;
    }

    public Id getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Embeddable
    public static class Id implements Serializable {
        @NotBlank
        @Column(name = "card_id")
        private String cardId;

        @NotBlank
        @Column(name = "wallet_name")
        @Enumerated(EnumType.STRING)
        private ExternalWallet walletName;

        protected Id() {
        }

        public Id(@NotBlank String cardId, @NotBlank ExternalWallet walletName) {
            this.cardId = cardId;
            this.walletName = walletName;
        }

        public String getCardId() {
            return cardId;
        }

        public ExternalWallet getWalletName() {
            return walletName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(cardId, id.cardId) &&
                    Objects.equals(walletName, id.walletName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cardId, walletName);
        }
    }

}
