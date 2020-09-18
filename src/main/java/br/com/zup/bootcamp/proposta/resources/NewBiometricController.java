package br.com.zup.bootcamp.proposta.resources;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.BiometricRepository;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import br.com.zup.bootcamp.proposta.resources.in.NewBiometricRequest;

@RestController
public class NewBiometricController {

    private final Logger logger = LoggerFactory.getLogger(NewBiometricController.class);
    private final BiometricRepository biometricRepository;
    private final CardRepository cardRepository;

    public NewBiometricController(BiometricRepository biometricRepository,
            CardRepository cardRepository) {
        this.biometricRepository = biometricRepository;
        this.cardRepository = cardRepository;
    }

    @PostMapping("/api/card/{cardId}/biometric")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBiometric(@PathVariable("cardId") String cardId,
            @Valid @RequestBody NewBiometricRequest biometricRequest) {
        logger.info("Creating new biometric for card {}", cardId);

        final Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "card not found"));

        biometricRepository.save(biometricRequest.toModel(card));
    }

}
