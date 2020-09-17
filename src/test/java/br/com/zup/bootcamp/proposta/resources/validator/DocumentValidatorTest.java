package br.com.zup.bootcamp.proposta.resources.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.zup.bootcamp.proposta.resources.validator.CNPJErrors;
import br.com.zup.bootcamp.proposta.resources.validator.CNPJValidator;
import br.com.zup.bootcamp.proposta.resources.validator.CPFErrors;
import br.com.zup.bootcamp.proposta.resources.validator.CPFValidator;
import br.com.zup.bootcamp.proposta.resources.validator.DocumentValidator;

class DocumentValidatorTest {
    @Test
    @DisplayName("documento vazio")
    void emptyDocumentInvalid() {
        DocumentValidator documentValidator = new DocumentValidator(null, null);
        String cpf = "";
        boolean isValid = documentValidator.isValidDocument(cpf);
        Assertions.assertFalse(isValid);
    }

    @Test
    @DisplayName("documento tamanho invalido")
    void sizeDocumentInvalid() {
        DocumentValidator documentValidator = new DocumentValidator(null, null);
        String cpf = "123456789";
        boolean isValid = documentValidator.isValidDocument(cpf);
        Assertions.assertFalse(isValid);
    }

    @Test
    @DisplayName("cpf valido")
    void validCpf() {
        final CPFValidator cpfValidator = Mockito.mock(CPFValidator.class);
        final CNPJValidator cnpjValidator = Mockito.mock(CNPJValidator.class);
        DocumentValidator documentValidator = new DocumentValidator(cnpjValidator, cpfValidator);

        String cpf = "34245299584";
        Mockito.when(cpfValidator.validate(Mockito.anyString())).then(a -> {
            String argCpf = a.getArgument(0);
            if (cpf.equals(argCpf)) return null;
            return CPFErrors.INVALID_DIGITS;
        });
        boolean isValid = documentValidator.isValidDocument(cpf);
        Assertions.assertTrue(isValid);

        Mockito.verify(cpfValidator).validate(cpf);
        Mockito.verify(cnpjValidator, Mockito.never()).validate(cpf);
    }

    @Test
    @DisplayName("cpf invalido")
    void invalidCpf() {
        final CPFValidator cpfValidator = Mockito.mock(CPFValidator.class);
        final CNPJValidator cnpjValidator = Mockito.mock(CNPJValidator.class);
        DocumentValidator documentValidator = new DocumentValidator(cnpjValidator, cpfValidator);

        String cpf = "34245299583";
        Mockito.when(cpfValidator.validate(Mockito.anyString())).then(a -> {
            String argCpf = a.getArgument(0);
            if (cpf.equals(argCpf)) return CPFErrors.INVALID_DIGITS;
            return null;
        });
        boolean isValid = documentValidator.isValidDocument(cpf);
        Assertions.assertFalse(isValid);

        Mockito.verify(cpfValidator).validate(cpf);
        Mockito.verify(cnpjValidator, Mockito.never()).validate(cpf);
    }


    @Test
    @DisplayName("cnpj valido")
    void validCnpj() {
        final CPFValidator cpfValidator = Mockito.mock(CPFValidator.class);
        final CNPJValidator cnpjValidator = Mockito.mock(CNPJValidator.class);
        DocumentValidator documentValidator = new DocumentValidator(cnpjValidator, cpfValidator);

        String document = "48897882000166";
        Mockito.when(cnpjValidator.validate(Mockito.anyString())).then(a -> {
            String arg = a.getArgument(0);
            if (document.equals(arg)) return null;
            return CNPJErrors.INVALID_DIGITS;
        });
        boolean isValid = documentValidator.isValidDocument(document);
        Assertions.assertTrue(isValid);

        Mockito.verify(cpfValidator, Mockito.never()).validate(document);
        Mockito.verify(cnpjValidator, Mockito.only()).validate(document);
    }

    @Test
    @DisplayName("cnpj invalido")
    void invalidCnpj() {
        final CPFValidator cpfValidator = Mockito.mock(CPFValidator.class);
        final CNPJValidator cnpjValidator = Mockito.mock(CNPJValidator.class);
        DocumentValidator documentValidator = new DocumentValidator(cnpjValidator, cpfValidator);

        String document = "48897882000165";
        Mockito.when(cnpjValidator.validate(Mockito.anyString())).then(a -> {
            String argCpf = a.getArgument(0);
            if (document.equals(argCpf)) return CNPJErrors.INVALID_DIGITS;
            return null;

        });
        boolean isValid = documentValidator.isValidDocument(document);
        Assertions.assertFalse(isValid);

        Mockito.verify(cpfValidator, Mockito.never()).validate(document);
        Mockito.verify(cnpjValidator, Mockito.only()).validate(document);
    }
}
