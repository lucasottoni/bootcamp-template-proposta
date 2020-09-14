package br.com.zup.bootcamp.proposta.validator;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.resources.NewProposeRequest;

class DuplicateProposalValidatorTest {

    @Test
    @DisplayName("has duplicate document")
    void testDuplicated() {
        ProposeRepository repository = Mockito.mock(ProposeRepository.class);
        DuplicateProposalValidator validator = new DuplicateProposalValidator(repository);

        Propose row = new Propose();

        Mockito.when(repository.findByDocument("123")).thenReturn(Optional.of(row));

        NewProposeRequest request = new NewProposeRequest(
                "123",
                "",
                "",
                "",
                BigDecimal.ONE
        );

        boolean result = validator.hasDuplicateDocument(request);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("has no duplicate document")
    void testNoDuplicate() {
        ProposeRepository repository = Mockito.mock(ProposeRepository.class);
        DuplicateProposalValidator validator = new DuplicateProposalValidator(repository);

        Mockito.when(repository.findByDocument("123")).thenReturn(Optional.empty());

        NewProposeRequest request = new NewProposeRequest(
                "123",
                "",
                "",
                "",
                BigDecimal.ONE
        );

        boolean result = validator.hasDuplicateDocument(request);

        Assertions.assertFalse(result);
    }
}
