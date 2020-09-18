package br.com.zup.bootcamp.proposta.resources.in;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;

import br.com.zup.bootcamp.proposta.model.Biometric;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.resources.constraint.Base64Constraint;

public class NewBiometricRequest {

    @NotBlank
    @Base64Constraint
    private final String biometric;

    @JsonCreator
    public NewBiometricRequest(@NotBlank @Base64Constraint String biometric) {
        this.biometric = biometric;
    }

    public String getBiometric() {
        return biometric;
    }

    public Biometric toModel(Card card) {
        return new Biometric(card, this.biometric);
    }
}
