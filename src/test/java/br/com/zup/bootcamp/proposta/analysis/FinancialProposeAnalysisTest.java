package br.com.zup.bootcamp.proposta.analysis;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.bootcamp.proposta.analisys.FinancialProposeAnalysis;
import br.com.zup.bootcamp.proposta.integration.financial.AnalysisRequest;
import br.com.zup.bootcamp.proposta.integration.financial.AnalysisResponse;
import br.com.zup.bootcamp.proposta.integration.financial.FinancialClientApi;
import br.com.zup.bootcamp.proposta.model.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Propose;

class FinancialProposeAnalysisTest {

    private final FinancialClientApi clientApi = Mockito.mock(FinancialClientApi.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("should get COM_RESTRICAO")
    void notApproved() {
        FinancialProposeAnalysis financialProposeAnalysis = new FinancialProposeAnalysis(clientApi, objectMapper);

        Propose propose = new Propose(
                "123",
                "e@e.com.br",
                "nome",
                "endereço",
                BigDecimal.ONE
        );

        AnalysisResponse declinedResponse = new AnalysisResponse();
        declinedResponse.setResultadoSolicitacao("COM_RESTRICAO");

        Mockito.when(clientApi.callAnalysis(Mockito.any(AnalysisRequest.class))).thenReturn(declinedResponse);
        final AnalysisStatus analysisStatus = financialProposeAnalysis.execute(propose);

        Assertions.assertEquals(AnalysisStatus.NOT_ELIGIBLE, analysisStatus);

        Mockito.verify(clientApi, Mockito.only()).callAnalysis(Mockito.any());
    }

    @Test
    @DisplayName("should get SEM_RESTRICAO")
    void approved() {
        FinancialProposeAnalysis financialProposeAnalysis = new FinancialProposeAnalysis(clientApi, objectMapper);

        Propose propose = new Propose(
                "123",
                "e@e.com.br",
                "nome",
                "endereço",
                BigDecimal.ONE
        );

        AnalysisResponse declinedResponse = new AnalysisResponse();
        declinedResponse.setResultadoSolicitacao("SEM_RESTRICAO");

        Mockito.when(clientApi.callAnalysis(Mockito.any(AnalysisRequest.class))).thenReturn(declinedResponse);
        final AnalysisStatus analysisStatus = financialProposeAnalysis.execute(propose);

        Assertions.assertEquals(AnalysisStatus.ELIGIBLE, analysisStatus);
        Mockito.verify(clientApi, Mockito.only()).callAnalysis(Mockito.any());
    }
}
