package br.com.zup.bootcamp.proposta.model;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.util.Assert;

import br.com.zup.bootcamp.proposta.model.enumeration.AnalysisStatus;

@Entity
public class Propose {
    @Id
    private String id;
    @Column
    @NotBlank
    private String document;
    @Column
    @NotBlank
    private String email;
    @Column
    @NotBlank
    private String name;
    @Column
    @NotBlank
    private String address;
    @Column
    @NotNull
    @Positive
    private BigDecimal salary;
    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private AnalysisStatus financialAnalysisStatus;
    @OneToOne
    private Card card;

    protected Propose() {

    }

    public Propose(@NotBlank String document, @NotBlank String email,
            @NotBlank String name, @NotBlank String address,
            @NotNull @Positive BigDecimal salary) {
        this.id = UUID.randomUUID().toString();
        this.document = document;
        this.email = email;
        this.name = name;
        this.address = address;
        this.salary = salary;
        this.financialAnalysisStatus = AnalysisStatus.NOT_ELIGIBLE;
    }

    public Propose(@NotBlank String document, @NotBlank String email,
            @NotBlank String name, @NotBlank String address,
            @NotNull @Positive BigDecimal salary,
            @NotNull AnalysisStatus status, Card card) {
        this.id = UUID.randomUUID().toString();
        this.document = document;
        this.email = email;
        this.name = name;
        this.address = address;
        this.salary = salary;
        this.financialAnalysisStatus = status;
        this.card = card;
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
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

    public Card getCard() {
        return card;
    }

    public AnalysisStatus getFinancialAnalysisStatus() {
        return this.financialAnalysisStatus;
    }

    public void changeFinancialAnalysis(AnalysisStatus newAnalysis) {
        Assert.isTrue(this.financialAnalysisStatus == AnalysisStatus.NOT_ELIGIBLE, "cannot change already eligible");
        this.financialAnalysisStatus = newAnalysis;
    }

    public void setCard(Card card) {
        Assert.isTrue(this.financialAnalysisStatus == AnalysisStatus.ELIGIBLE, "cannot get card for non eligible");
        Assert.isNull(this.card, "card has already been generated");
        this.card = card;
    }
}
