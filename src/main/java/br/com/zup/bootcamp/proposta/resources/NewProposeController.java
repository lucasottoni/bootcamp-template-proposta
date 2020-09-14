package br.com.zup.bootcamp.proposta.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.validator.DuplicateProposalValidator;
import br.com.zup.bootcamp.proposta.validator.NewProposeRequestValidator;

@RestController
public class NewProposeController {

    private final NewProposeRequestValidator validator;
    private final DuplicateProposalValidator duplicateProposalValidator;
    private final ProposeRepository proposeRepository;

    public NewProposeController(NewProposeRequestValidator validator,
            DuplicateProposalValidator duplicateProposalValidator,
            ProposeRepository proposeRepository) {
        this.validator = validator;
        this.duplicateProposalValidator = duplicateProposalValidator;
        this.proposeRepository = proposeRepository;
    }

    @InitBinder
    void initBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }

    @PostMapping("/propose")
    @Transactional
    public ResponseEntity<Void> createNewPropose(
            @Valid @RequestBody NewProposeRequest request) {

        if (duplicateProposalValidator.hasDuplicateDocument(request)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Propose propose = request.toModel();
        proposeRepository.save(propose);

        return ResponseEntity.created(URI.create("/propose/" + propose.getId())).build();
    }

}
