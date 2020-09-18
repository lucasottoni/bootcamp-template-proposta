package br.com.zup.bootcamp.proposta.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.service.analysis.FinancialProposeAnalysis;
import br.com.zup.bootcamp.proposta.model.enumeration.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.TransactionWrapper;
import br.com.zup.bootcamp.proposta.resources.in.NewProposeRequest;
import br.com.zup.bootcamp.proposta.resources.validator.DuplicateProposalValidator;
import br.com.zup.bootcamp.proposta.resources.validator.NewProposeRequestValidator;

@RestController
public class NewProposeController {

    private final NewProposeRequestValidator validator;
    private final DuplicateProposalValidator duplicateProposalValidator;
    private final TransactionWrapper transactionWrapper;
    private final FinancialProposeAnalysis financialProposeAnalysis;

    public NewProposeController(NewProposeRequestValidator validator,
            DuplicateProposalValidator duplicateProposalValidator,
            TransactionWrapper transactionWrapper,
            FinancialProposeAnalysis financialProposeAnalysis) {
        this.validator = validator;
        this.duplicateProposalValidator = duplicateProposalValidator;
        this.transactionWrapper = transactionWrapper;
        this.financialProposeAnalysis = financialProposeAnalysis;
    }

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }

    @PostMapping("/api/propose")
    public ResponseEntity<Void> createNewPropose(
            @Valid @RequestBody NewProposeRequest request) {

        if (duplicateProposalValidator.hasDuplicateDocument(request)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "duplicated document");
        }

        Propose propose = request.toModel();
        transactionWrapper.create(propose);

        final AnalysisStatus analysisStatus = financialProposeAnalysis.execute(propose);
        propose.changeFinancialAnalysis(analysisStatus);
        transactionWrapper.update(propose);

        return ResponseEntity.created(URI.create("/propose/" + propose.getId())).build();
    }

}
