package br.com.zup.bootcamp.proposta.resources.validator;

import org.springframework.stereotype.Component;

@Component
public class CPFValidator {

    public CPFErrors validate(String cpf) {
        if (cpf == null || "".equals(cpf)) {
            return CPFErrors.INVALID_DIGITS;
        }

        if (cpf.length() != 11) {
            return CPFErrors.INVALID_DIGITS;
        }

        if (isAllRepeated(cpf)) {
            return CPFErrors.REPEATED_DIGITS;
        }

        if (!isCheckDigitValid(cpf)) {
            return CPFErrors.INVALID_CHECK_DIGITS;
        }

        return null;
    }

    private boolean isCheckDigitValid(String cpf) {
        char digit10 = getDigit10(cpf);
        char digit11 = getDigit11(cpf);

        return digit10 == cpf.charAt(9) && digit11 == cpf.charAt(10);
    }

    private char getDigit10(String cpf) {
        int sum = 0;
        int weight = 10;
        for (int i = 0; i < 9; ++i) {
            int num = charToInt(cpf.charAt(i));
            sum += (num * weight);
            weight--;
        }

        int mod = 11 - (sum % 11);
        if (mod == 10 || mod == 11)
            return '0';
        return intToChar(mod);
    }

    private char getDigit11(String cpf) {
        int sum = 0;
        int weight = 11;
        for (int i = 0; i < 10; ++i) {
            int num = charToInt(cpf.charAt(i));
            sum += (num * weight);
            weight--;
        }

        int mod = 11 - (sum % 11);
        if (mod == 10 || mod == 11)
            return '0';
        return intToChar(mod);
    }

    private int charToInt(char c) {
        return c - '0';
    }

    private char intToChar(int i) {
        return (char) (i + '0');
    }

    private boolean isAllRepeated(String cpf) {
        final char start = cpf.charAt(0);

        for (int i = 1; i < cpf.length(); ++i) {
            if (cpf.charAt(i) != start)
                return false;
        }
        return true;
    }
}
