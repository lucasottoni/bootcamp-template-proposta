package br.com.zup.bootcamp.proposta.integration.card;

import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.zup.bootcamp.proposta.model.TravelNotice;

public class TravelNoticeRequest {
    @NotBlank
    @JsonProperty("destino")
    private final String destination;
    @NotNull
    @JsonProperty("validoAte")
    private final LocalDate dueAt;

    public TravelNoticeRequest(@NotBlank String destination,
            @NotNull LocalDate dueAt) {
        this.destination = destination;
        this.dueAt = dueAt;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDueAt() {
        return dueAt;
    }

    public static TravelNoticeRequest of(TravelNotice travelNotice) {
        return new TravelNoticeRequest(
                travelNotice.getTravelDestination(),
                travelNotice.getTravelEnd()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelNoticeRequest that = (TravelNoticeRequest) o;
        return Objects.equals(destination, that.destination) &&
                Objects.equals(dueAt, that.dueAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, dueAt);
    }
}
