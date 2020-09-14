package br.com.zup.bootcamp.proposta.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.resources.NewProposeRequest;

@Component
public class DuplicateProposalValidator {

    private final ProposeRepository proposeRepository;

    public DuplicateProposalValidator(ProposeRepository proposeRepository) {
        this.proposeRepository = proposeRepository;
    }

    public boolean hasDuplicateDocument(NewProposeRequest request) {
        Optional<Propose> propose = proposeRepository.findByDocument(request.getDocument());
        return (propose.isPresent());
    }
}
