package br.com.zup.bootcamp.proposta.resources.util;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ClientIpExtractorTest {

    @Test
    @DisplayName("Get ip from remote address")
    void remoteAddr() {

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getRemoteAddr()).thenReturn("REMOTE-ADDR");

        ClientIpExtractor clientIpExtractor = new ClientIpExtractor();
        @NotBlank final String clientIp = clientIpExtractor.getClientIp(request);

        Assertions.assertEquals("REMOTE-ADDR", clientIp);
    }
    @Test
    @DisplayName("Get ip from header ")
    void remoteHeader() {

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn("FROM_HEADER_IP");

        ClientIpExtractor clientIpExtractor = new ClientIpExtractor();
        @NotBlank final String clientIp = clientIpExtractor.getClientIp(request);

        Assertions.assertEquals("FROM_HEADER_IP", clientIp);
    }
}
