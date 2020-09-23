package br.com.zup.bootcamp.proposta.resources.integrated;

import java.math.BigDecimal;

import javax.transaction.Transactional;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;
import br.com.zup.bootcamp.proposta.resources.in.NewProposeRequest;
import io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = TracerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class NewProposeControllerIntegratedTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProposeRepository proposeRepository;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Transactional
    @Test
    @DisplayName("Create new proposal")
    void newProposal() throws Exception {

        NewProposeRequest request = new NewProposeRequest(
                "99342409040",
                "teste@teste.com.br",
                "nome do teste",
                "Rua X, 3883",
                new BigDecimal("10900.99")
        );

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, CoreMatchers.startsWith("/propose/")));

    }

    @Transactional
    @Test
    @DisplayName("Create new proposal analysis failure")
    void newProposalAnalysis() throws Exception {

        NewProposeRequest request = new NewProposeRequest(
                "34245299584",
                "teste@teste.com.br",
                "nome do teste",
                "Rua X, 3883",
                new BigDecimal("10900.99")
        );

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, CoreMatchers.startsWith("/propose/")));

    }

    @Test
    @DisplayName("New Proposal Field Empty")
    void badRequest() throws Exception {

        NewProposeRequest request = new NewProposeRequest(
                "34245299584",
                null,
                "nome do teste",
                "Rua X, 3883",
                new BigDecimal("10900.99")
        );

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    @DisplayName("New Proposal Invalid Document")
    void badRequestDocument() throws Exception {

        NewProposeRequest request = new NewProposeRequest(
                "0000000000",
                "email@email.com",
                "nome do teste",
                "Rua X, 3883",
                new BigDecimal("10900.99")
        );

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Transactional
    @Test
    @DisplayName("New Proposal Duplicate Document")
    void duplicateDocument() throws Exception {

        NewProposeRequest request = new NewProposeRequest(
                "56697333049",
                "email@email.com",
                "nome do teste",
                "Rua X, 3883",
                new BigDecimal("10900.99")
        );

        Propose propose = new Propose(
                request.getDocument(),
                "a@a.com.br",
                "nomeX",
                "endereco",
                BigDecimal.ONE
        );
        proposeRepository.save(propose);

        mvc.perform(
                MockMvcRequestBuilders
                        .post(getUrl())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

    }

    private String getUrl() {
        return "/api/propose";
    }

    private String asJsonString(NewProposeRequest request) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(request);
    }
}
