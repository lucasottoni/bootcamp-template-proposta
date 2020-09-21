package br.com.zup.bootcamp.proposta.model;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class TravelNotice {

    @Id
    private String id;

    @NotNull
    @ManyToOne
    private Card card;

    @NotBlank
    @Column
    private String travelDestination;

    @NotNull
    @Column
    private LocalDate travelEnd;

    @OneToOne(cascade = CascadeType.PERSIST)
    @NotNull
    private AuditInfo auditInfo;

    protected TravelNotice() {
    }

    public TravelNotice(@NotNull Card card,
            @NotBlank String travelDestination,
            @NotNull LocalDate travelEnd,
            @NotNull AuditInfo auditInfo) {
        this.id = UUID.randomUUID().toString();
        this.card = card;
        this.travelDestination = travelDestination;
        this.travelEnd = travelEnd;
        this.auditInfo = auditInfo;
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

    public String getTravelDestination() {
        return travelDestination;
    }

    public LocalDate getTravelEnd() {
        return travelEnd;
    }

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }
}
