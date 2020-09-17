package br.com.zup.bootcamp.proposta.resources.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DocumentValidator {

    private final CNPJValidator cnpjValidator;
    private final CPFValidator cpfValidator;

    static final int CPF_LENGTH = 11;
    static final int CNPJ_LENGTH = 14;

    public DocumentValidator(CNPJValidator cnpjValidator,
            CPFValidator cpfValidator) {
        this.cnpjValidator = cnpjValidator;
        this.cpfValidator = cpfValidator;
    }

    public boolean isValidDocument(String document) {
        if (StringUtils.isEmpty(document)) {
            return false;
        } else if (document.length() == CPF_LENGTH) {
            return isValidCpf(document);
        } else if (document.length() == CNPJ_LENGTH) {
            return isValidCnpj(document);
        }
        return false;
    }

    private boolean isValidCnpj(String document) {
        CNPJErrors error = cnpjValidator.validate(document);
        return (error == null);
    }

    private boolean isValidCpf(String document) {
        CPFErrors error = cpfValidator.validate(document);
        return (error == null);
    }
}
