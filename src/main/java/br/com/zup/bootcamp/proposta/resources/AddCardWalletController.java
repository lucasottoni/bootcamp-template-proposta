package br.com.zup.bootcamp.proposta.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.bootcamp.proposta.exception.CardNotFoundException;
import br.com.zup.bootcamp.proposta.exception.WalletAlreadyAssociatedException;
import br.com.zup.bootcamp.proposta.model.CardWallet;
import br.com.zup.bootcamp.proposta.resources.in.NewCardWalletRequest;
import br.com.zup.bootcamp.proposta.service.card.CardWalletOrchestrator;

@RestController
public class AddCardWalletController {

    private final CardWalletOrchestrator orchestrator;

    public AddCardWalletController(CardWalletOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/api/card/{cardId}/wallet")
    public ResponseEntity<Void> addWallet(@PathVariable("cardId") String cardId,
            @Valid @RequestBody NewCardWalletRequest request) throws URISyntaxException {
        CardWallet cardWallet = request.toModel(cardId);

        Optional<CardWallet> response;
        try {
            response = orchestrator.associateWallet(cardWallet);
        } catch (WalletAlreadyAssociatedException walletAlreadyAssociatedException) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "wallet already associated to card");
        } catch (CardNotFoundException cardNotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "card not found");
        }

        if (response.isPresent()) {
            return ResponseEntity.created(new URI("/api/wallet/" + response.get().getExternalId())).build();
        }

        throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "could not associate at external api");
    }
}
