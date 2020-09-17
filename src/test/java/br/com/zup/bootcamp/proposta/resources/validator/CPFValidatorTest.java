package br.com.zup.bootcamp.proposta.resources.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.zup.bootcamp.proposta.resources.validator.CPFErrors;
import br.com.zup.bootcamp.proposta.resources.validator.CPFValidator;

class CPFValidatorTest {

    @Test
    @DisplayName("não aceita quando o campo é vazio")
    void shouldNotAcceptEmpty() {
        CPFValidator cpfValidator = new CPFValidator();
        CPFErrors error = cpfValidator.validate("");
        Assertions.assertEquals(CPFErrors.INVALID_DIGITS, error);
    }

    @Test
    @DisplayName("não aceita tamanho inválido menos que 11")
    void lessThan11Digits() {
        CPFValidator cpfValidator = new CPFValidator();
        CPFErrors error = cpfValidator.validate("342452995");
        Assertions.assertEquals(CPFErrors.INVALID_DIGITS, error);
    }


    @Test
    @DisplayName("não aceita tamanho inválido maior que 11")
    void moreThan11Digits() {
        CPFValidator cpfValidator = new CPFValidator();
        CPFErrors error = cpfValidator.validate("342452995841");
        Assertions.assertEquals(CPFErrors.INVALID_DIGITS, error);
    }

    @Test
    @DisplayName("CPF com números repetidos")
    void shouldNotAcceptDuplicated() {
        CPFValidator cpfValidator = new CPFValidator();
        CPFErrors error = cpfValidator.validate("00000000000");
        Assertions.assertEquals(CPFErrors.REPEATED_DIGITS, error);
    }

    @Test
    @DisplayName("CPF inválido")
    void invalidCpf() {
        CPFValidator cpfValidator = new CPFValidator();
        CPFErrors error = cpfValidator.validate("34245299583");
        Assertions.assertEquals(CPFErrors.INVALID_CHECK_DIGITS, error);
    }

    @Test
    @DisplayName("CPF Válido")
    void isValidCpf() {
        CPFValidator cpfValidator = new CPFValidator();
        CPFErrors error = cpfValidator.validate("34245299584");
        Assertions.assertNull(error);
    }

}
