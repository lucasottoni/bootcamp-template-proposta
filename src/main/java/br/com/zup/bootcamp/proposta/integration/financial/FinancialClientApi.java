package br.com.zup.bootcamp.proposta.integration.financial;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "financialClient", url="${feign.financial.url}")
public interface FinancialClientApi {
    @PostMapping("/api/solicitacao")
    AnalysisResponse callAnalysis(AnalysisRequest analysisRequest);
}
