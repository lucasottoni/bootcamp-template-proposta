package br.com.zup.bootcamp.proposta.resources.in;

import java.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.bootcamp.proposta.model.AuditInfo;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.TravelNotice;

public class NewTravelNoticeRequest {
    @NotBlank
    private final String destination;
    @NotNull
    @Future
    private final LocalDate endDate;

    public NewTravelNoticeRequest(@NotBlank String destination,
            @NotNull @Future LocalDate endDate) {
        this.destination = destination;
        this.endDate = endDate;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public TravelNotice toModel(Card card, AuditInfo auditInfo) {
        return new TravelNotice(
                card,
                this.destination,
                this.endDate,
                auditInfo
        );
    }
}
