package br.com.zup.bootcamp.proposta.validator;

import org.springframework.stereotype.Component;

@Component
public class CNPJValidator {

    public CNPJErrors validate(String cnpj) {
        if (cnpj == null || "".equals(cnpj)) {
            return CNPJErrors.INVALID_DIGITS;
        }

        if (cnpj.length() != 14) {
            return CNPJErrors.INVALID_DIGITS;
        }

        if (isAllRepeated(cnpj)) {
            return CNPJErrors.REPEATED_DIGITS;
        }

        if (!isCheckDigitValid(cnpj)) {
            return CNPJErrors.INVALID_CHECK_DIGITS;
        }

        return null;
    }

    private boolean isCheckDigitValid(String cnpj) {
        char digit13 = getDigit13(cnpj);
        char digit14 = getDigit14(cnpj);

        return digit13 == cnpj.charAt(12) && digit14 == cnpj.charAt(13);
    }

    private char getDigit13(String cnpj) {
        int sum = 0;
        int weight = 2;
        for(int i = 11; i >= 0; --i) {
            int num = charToInt(cnpj.charAt(i));
            sum += (num * weight);
            weight++;
            if (weight == 10) {
                weight = 2;
            }
        }

        int mod = (sum % 11);
        if (mod == 0 || mod == 1)
            return '0';
        mod = 11 - mod;
        return intToChar(mod);
    }

    private char getDigit14(String cnpj) {
        int sum = 0;
        int weight = 2;
        for(int i = 12; i >= 0; --i) {
            int num = charToInt(cnpj.charAt(i));
            sum += (num * weight);
            weight++;
            if (weight == 10) {
                weight = 2;
            }
        }

        int mod = (sum % 11);
        if (mod == 0 || mod == 1)
            return '0';
        mod = 11 - mod;
        return intToChar(mod);
    }

    private int charToInt(char c) {
        return c - '0';
    }

    private char intToChar(int i) {
        return (char)(i + '0');
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
