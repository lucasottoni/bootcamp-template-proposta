package br.com.zup.bootcamp.proposta.resources;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.BiometricRepository;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.resources.in.NewBiometricRequest;

@RestController
public class NewBiometricController {

    private final Logger logger = LoggerFactory.getLogger(NewBiometricController.class);
    private final BiometricRepository biometricRepository;
    private final ProposeRepository proposeRepository;

    public NewBiometricController(BiometricRepository biometricRepository,
            ProposeRepository proposeRepository) {
        this.biometricRepository = biometricRepository;
        this.proposeRepository = proposeRepository;
    }

    @PostMapping("/card/{cardId}/biometric")
    public void addBiometric(@PathVariable("cardId") String cardId,
            @Valid @RequestBody NewBiometricRequest biometricRequest) {
        logger.info("Creating new biometric for card {}", cardId);

        final Propose propose = proposeRepository.getByCardId(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "propose not found"));

        biometricRepository.save(biometricRequest.toModel(propose));
    }

}
