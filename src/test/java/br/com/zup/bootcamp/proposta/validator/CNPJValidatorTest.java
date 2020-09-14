package br.com.zup.bootcamp.proposta.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CNPJValidatorTest {

    @Test
    @DisplayName("não aceita quando o campo é vazio")
    void shouldNotAcceptEmpty() {
        CNPJValidator CNPJValidator = new CNPJValidator();
        CNPJErrors error = CNPJValidator.validate("");
        Assertions.assertEquals(CNPJErrors.INVALID_DIGITS, error);
    }

    @Test
    @DisplayName("não aceita tamanho inválido menos que 14")
    void lessThan14Digits() {
        CNPJValidator CNPJValidator = new CNPJValidator();
        CNPJErrors error = CNPJValidator.validate("4889788200016");
        Assertions.assertEquals(CNPJErrors.INVALID_DIGITS, error);
    }


    @Test
    @DisplayName("não aceita tamanho inválido maior que 14")
    void moreThan14Digits() {
        CNPJValidator CNPJValidator = new CNPJValidator();
        CNPJErrors error = CNPJValidator.validate("488978820001666");
        Assertions.assertEquals(CNPJErrors.INVALID_DIGITS, error);
    }

    @Test
    @DisplayName("CNPJ com números repetidos")
    void shouldNotAcceptDuplicated() {
        CNPJValidator CNPJValidator = new CNPJValidator();
        CNPJErrors error = CNPJValidator.validate("00000000000000");
        Assertions.assertEquals(CNPJErrors.REPEATED_DIGITS, error);
    }

    @Test
    @DisplayName("CNPJ inválido")
    void invalidCNPJ() {
        CNPJValidator CNPJValidator = new CNPJValidator();
        CNPJErrors error = CNPJValidator.validate("48897882000165");
        Assertions.assertEquals(CNPJErrors.INVALID_CHECK_DIGITS, error);
    }

    @Test
    @DisplayName("CNPJ Válido")
    void isValidCNPJ() {
        CNPJValidator CNPJValidator = new CNPJValidator();
        CNPJErrors error = CNPJValidator.validate("48897882000166");
        Assertions.assertNull(error);
    }
}
