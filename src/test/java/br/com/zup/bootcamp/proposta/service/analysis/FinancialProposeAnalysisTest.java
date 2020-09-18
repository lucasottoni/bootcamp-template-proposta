package br.com.zup.bootcamp.proposta.service.analysis;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.bootcamp.proposta.integration.financial.AnalysisRequest;
import br.com.zup.bootcamp.proposta.integration.financial.AnalysisResponse;
import br.com.zup.bootcamp.proposta.integration.financial.FinancialClientApi;
import br.com.zup.bootcamp.proposta.model.enumeration.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Propose;
import feign.FeignException;

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

    @Test
    @DisplayName("should get failure integration with invalid payload exception")
    void failureOnParsingErrorIntegration() {
        FinancialProposeAnalysis financialProposeAnalysis = new FinancialProposeAnalysis(clientApi, objectMapper);

        Propose propose = new Propose(
                "123",
                "e@e.com.br",
                "nome",
                "endereço",
                BigDecimal.ONE
        );

        FeignException.FeignClientException error = Mockito.mock(FeignException.FeignClientException.class);

        Mockito.when(clientApi.callAnalysis(Mockito.any(AnalysisRequest.class))).thenThrow(error);
        Mockito.when(error.contentUTF8()).thenReturn("INVALID PAYLOAD");
        final AnalysisStatus analysisStatus = financialProposeAnalysis.execute(propose);

        Assertions.assertEquals(AnalysisStatus.NOT_ELIGIBLE, analysisStatus);
        Mockito.verify(clientApi, Mockito.only()).callAnalysis(Mockito.any());
    }

    @Test
    @DisplayName("should get failure integration with right payload exception")
    void failureIntegration() throws JsonProcessingException {
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

        FeignException.FeignClientException error = Mockito.mock(FeignException.FeignClientException.class);
        ObjectMapper objectMapper = new ObjectMapper();

        Mockito.when(clientApi.callAnalysis(Mockito.any(AnalysisRequest.class))).thenThrow(error);
        Mockito.when(error.contentUTF8()).thenReturn(objectMapper.writeValueAsString(declinedResponse));

        final AnalysisStatus analysisStatus = financialProposeAnalysis.execute(propose);

        Assertions.assertEquals(AnalysisStatus.NOT_ELIGIBLE, analysisStatus);
        Mockito.verify(clientApi, Mockito.only()).callAnalysis(Mockito.any());
    }
}
