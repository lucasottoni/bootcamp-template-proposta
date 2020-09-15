package br.com.zup.bootcamp.proposta.advice;

import java.util.List;

public class ErrorMessage {
    private final List<String> messages;

    public ErrorMessage(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
