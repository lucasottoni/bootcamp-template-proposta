package br.com.zup.bootcamp.proposta.integration.card;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockingCardRequest {
    @NotNull
    @JsonProperty("sistemaResponsavel")
    private final String ownerSystem;

    @JsonCreator
    public BlockingCardRequest(@NotNull String ownerSystem) {
        this.ownerSystem = ownerSystem;
    }

    public String getOwnerSystem() {
        return ownerSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockingCardRequest that = (BlockingCardRequest) o;
        return Objects.equals(ownerSystem, that.ownerSystem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerSystem);
    }
}
