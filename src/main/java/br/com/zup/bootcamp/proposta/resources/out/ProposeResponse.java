package br.com.zup.bootcamp.proposta.resources.out;

import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.com.zup.bootcamp.proposta.model.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;

public class ProposeResponse {
    @NotBlank
    private final String id;
    @NotNull
    private final AnalysisStatus financialStatus;
    private final String cardId;

    public ProposeResponse(@NotBlank String id,
            @NotNull AnalysisStatus financialStatus, String cardId) {
        this.id = id;
        this.financialStatus = financialStatus;
        this.cardId = cardId;
    }

    public static ProposeResponse from(@NotNull Propose propose) {
        return new ProposeResponse(
                propose.getId(),
                propose.getFinancialAnalysisStatus(),
                Optional.ofNullable(propose.getCard()).map(Card::getId).orElse(null)
        );
    }

    public String getId() {
        return id;
    }

    public AnalysisStatus getFinancialStatus() {
        return financialStatus;
    }

    public String getCardId() {
        return cardId;
    }
}
