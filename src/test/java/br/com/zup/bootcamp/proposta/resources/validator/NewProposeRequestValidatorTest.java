package br.com.zup.bootcamp.proposta.resources.validator;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.validation.Errors;

import br.com.zup.bootcamp.proposta.resources.in.NewProposeRequest;
import br.com.zup.bootcamp.proposta.resources.validator.DocumentValidator;
import br.com.zup.bootcamp.proposta.resources.validator.NewProposeRequestValidator;

class NewProposeRequestValidatorTest {

    @Test
    @DisplayName("documento invalido")
    void invalidDocument() {
        DocumentValidator validator = Mockito.mock(DocumentValidator.class);
        Mockito.when(validator.isValidDocument(Mockito.anyString())).thenReturn(false);

        NewProposeRequestValidator requestValidator = new NewProposeRequestValidator(validator);

        NewProposeRequest request = new NewProposeRequest(
                "DOCUMENT",
                "EMAIL@EMIAL.COM",
                "NAME",
                "ADDRESS",
                new BigDecimal("1920.22")
        );

        Errors errors = Mockito.mock(Errors.class);
        requestValidator.validate(request, errors);

        Mockito.verify(errors).rejectValue("document", "invalid");
    }

    @Test
    @DisplayName("documento ok")
    void validDocument() {
        DocumentValidator validator = Mockito.mock(DocumentValidator.class);
        Mockito.when(validator.isValidDocument(Mockito.anyString())).thenReturn(true);

        NewProposeRequestValidator requestValidator = new NewProposeRequestValidator(validator);

        NewProposeRequest request = new NewProposeRequest(
                "DOCUMENT",
                "EMAIL@EMIAL.COM",
                "NAME",
                "ADDRESS",
                new BigDecimal("1920.22")
        );

        Errors errors = Mockito.mock(Errors.class);
        requestValidator.validate(request, errors);

        Mockito.verify(errors, Mockito.never()).rejectValue(Mockito.anyString(), Mockito.anyString());
    }
}
