package br.com.zup.bootcamp.proposta.resources;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.resources.out.ProposeResponse;

@RestController
public class ProposeFollowUpController {

    private final ProposeRepository proposeRepository;

    public ProposeFollowUpController(ProposeRepository proposeRepository) {
        this.proposeRepository = proposeRepository;
    }

    @GetMapping("/propose/{proposeId}")
    public ProposeResponse findById(@PathVariable("proposeId") String proposeId) {
        final Propose propose = proposeRepository.findById(proposeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PROPOSE NOT FOUND"));

        return ProposeResponse.from(propose);
    }
}
