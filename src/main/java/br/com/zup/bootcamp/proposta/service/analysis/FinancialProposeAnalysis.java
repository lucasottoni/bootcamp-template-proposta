package br.com.zup.bootcamp.proposta.service.analysis;

import java.text.MessageFormat;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.bootcamp.proposta.integration.financial.AnalysisRequest;
import br.com.zup.bootcamp.proposta.integration.financial.AnalysisResponse;
import br.com.zup.bootcamp.proposta.integration.financial.FinancialClientApi;
import br.com.zup.bootcamp.proposta.model.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Propose;
import feign.FeignException;

@Component
public class FinancialProposeAnalysis {

    private final Logger logger = LoggerFactory.getLogger(FinancialProposeAnalysis.class);

    private final FinancialClientApi financialClientApi;
    private final ObjectMapper objectMapper;

    public FinancialProposeAnalysis(
            FinancialClientApi financialClientApi, ObjectMapper objectMapper) {
        this.financialClientApi = financialClientApi;
        this.objectMapper = objectMapper;
    }

    public AnalysisStatus execute(@NotNull Propose propose) {
        AnalysisRequest request = AnalysisRequest.fromPropose(propose);
        Optional<AnalysisResponse> response = callIntegration(request);
        return response.map(AnalysisResponse::parseStatus).orElse(AnalysisStatus.NOT_ELIGIBLE);
    }

    private @NotNull Optional<AnalysisResponse> callIntegration(AnalysisRequest request) {
        try {
            AnalysisResponse response = financialClientApi.callAnalysis(request);
            return Optional.of(response);
        } catch (FeignException.FeignClientException fce) {
            logger.info(MessageFormat.format("There was an exception calling financial api: {0}", fce.getMessage()));
            return tryParseFailure(fce);
        }
    }

    private @NotNull Optional<AnalysisResponse> tryParseFailure(FeignException.FeignClientException fce) {
        String body = fce.contentUTF8();

        try {
            AnalysisResponse response = objectMapper.readValue(body, AnalysisResponse.class);
            return Optional.of(response);
        } catch (JsonProcessingException e) {
            logger.warn("Failure to parse financial analysis response", e);
        }
        return Optional.empty();
    }

}
