package br.com.zup.bootcamp.proposta.resources.in;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;

import br.com.zup.bootcamp.proposta.constraint.Base64Constraint;
import br.com.zup.bootcamp.proposta.model.Biometric;
import br.com.zup.bootcamp.proposta.model.Propose;

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

    public Biometric toModel(Propose propose) {
        return new Biometric(propose.getCardId(), this.biometric);
    }
}
