package br.com.zup.bootcamp.proposta.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.com.zup.bootcamp.proposta.resources.in.NewProposeRequest;

@Component
public class NewProposeRequestValidator implements Validator {

    private final DocumentValidator documentValidator;

    public NewProposeRequestValidator(DocumentValidator documentValidator) {
        this.documentValidator = documentValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return NewProposeRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        NewProposeRequest request = (NewProposeRequest) o;

        if (!request.isValidDocument(documentValidator)) {
            errors.rejectValue("document", "invalid");
        }

    }

}
