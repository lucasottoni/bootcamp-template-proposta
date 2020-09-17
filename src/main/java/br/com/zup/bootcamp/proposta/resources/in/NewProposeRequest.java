package br.com.zup.bootcamp.proposta.resources.in;


import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.resources.validator.DocumentValidator;

public class NewProposeRequest {

    @NotBlank
    private final String document;
    @Email
    @NotBlank
    private final String email;
    @NotBlank
    private final String name;
    @NotBlank
    private final String address;
    @NotNull
    @Positive
    private final BigDecimal salary;

    public NewProposeRequest(@NotBlank String document,
            @Email @NotBlank String email, @NotBlank String name,
            @NotBlank String address,
            @NotNull @Positive BigDecimal salary) {
        this.document = document;
        this.email = email;
        this.name = name;
        this.address = address;
        this.salary = salary;
    }

    public String getDocument() {
        return document;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public boolean isValidDocument(DocumentValidator documentValidator) {
        return documentValidator.isValidDocument(document);
    }

    public Propose toModel() {
        return new Propose(
                this.document,
                this.email,
                this.name,
                this.address,
                this.salary
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewProposeRequest request = (NewProposeRequest) o;
        return document.equals(request.document) &&
                email.equals(request.email) &&
                name.equals(request.name) &&
                address.equals(request.address) &&
                salary.equals(request.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, email, name, address, salary);
    }
}