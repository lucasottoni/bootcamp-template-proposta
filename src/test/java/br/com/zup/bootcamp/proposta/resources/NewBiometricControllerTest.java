package br.com.zup.bootcamp.proposta.resources;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.bootcamp.proposta.model.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.BiometricRepository;
import br.com.zup.bootcamp.proposta.repository.CardRepository;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.resources.in.NewBiometricRequest;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = NewBiometricController.class)
@AutoConfigureMockMvc(addFilters = false)
class NewBiometricControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private BiometricRepository biometricRepository;

    @Test
    @DisplayName("Save new biometric")
    void saveNew() throws Exception {
        String cardId = "CARD-123";

        Card card = new Card(
                cardId,
                new Propose("documento", "email@email.com", "nome", "enderco", BigDecimal.ONE),
                "titular X"
        );

        Mockito.when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        NewBiometricRequest request = new NewBiometricRequest(new String(Base64.getEncoder().encode("TESTE".getBytes())));

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(request))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    @DisplayName("Fail to save invalid biometric")
    void invalidBase64() throws Exception {
        String cardId = "CARD-123";
        NewBiometricRequest request = new NewBiometricRequest("INVALID BASE 64");

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(request))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @DisplayName("Fail to save not found card")
    void proposeNotFound() throws Exception {
        String cardId = "CARD-123";
        NewBiometricRequest request = new NewBiometricRequest(new String(Base64.getEncoder().encode("TESTE".getBytes())));

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl(cardId))
                        .content(asJsonString(request))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private String getUrl(String cardId) {
        return "/api/card/" + cardId + "/biometric";
    }

    private String asJsonString(NewBiometricRequest request) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(request);
    }
}
