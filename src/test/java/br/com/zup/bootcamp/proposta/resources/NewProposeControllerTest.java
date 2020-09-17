package br.com.zup.bootcamp.proposta.resources;

import java.math.BigDecimal;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.bootcamp.proposta.service.analysis.FinancialProposeAnalysis;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.TransactionWrapper;
import br.com.zup.bootcamp.proposta.resources.in.NewProposeRequest;
import br.com.zup.bootcamp.proposta.resources.validator.DocumentValidator;
import br.com.zup.bootcamp.proposta.resources.validator.DuplicateProposalValidator;
import br.com.zup.bootcamp.proposta.resources.validator.NewProposeRequestValidator;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NewProposeController.class)
class NewProposeControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private DuplicateProposalValidator duplicateProposalValidator;

    @MockBean
    private NewProposeRequestValidator proposeValidator;

    @MockBean
    private DocumentValidator documentValidator;

    @MockBean
    private FinancialProposeAnalysis financialProposeAnalysis;

    @MockBean
    private TransactionWrapper transactionWrapper;

    @BeforeEach
    void setup() {
        Mockito.when(proposeValidator.supports(NewProposeRequest.class)).thenReturn(true);
    }

    @Test
    @DisplayName("Create new proposal")
    void newProposal() throws Exception {

        NewProposeRequest request = new NewProposeRequest(
                "34245299584",
                "teste@teste.com.br",
                "nome do teste",
                "Rua X, 3883",
                new BigDecimal("10900.99")
        );

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/propose")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, CoreMatchers.startsWith("/propose/")));

        Mockito.verify(transactionWrapper).create(Mockito.any(Propose.class));
        Mockito.verify(transactionWrapper).update(Mockito.any(Propose.class));
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
                        .post("/propose")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(transactionWrapper, Mockito.never()).create(Mockito.any(Propose.class));
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

        ReflectionTestUtils.setField(proposeValidator, "documentValidator", documentValidator);
        Mockito.doCallRealMethod().when(proposeValidator).validate(Mockito.eq(request), Mockito.any(Errors.class));
        Mockito.when(documentValidator.isValidDocument(request.getDocument())).thenReturn(false);

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/propose")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(transactionWrapper, Mockito.never()).create(Mockito.any(Propose.class));
    }

    @Test
    @DisplayName("New Proposal Duplicate Document")
    void duplicateDocument() throws Exception {

        NewProposeRequest request = new NewProposeRequest(
                "34245299584",
                "email@email.com",
                "nome do teste",
                "Rua X, 3883",
                new BigDecimal("10900.99")
        );

        Mockito.when(duplicateProposalValidator.hasDuplicateDocument(request)).thenReturn(true);

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/propose")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .content(asJsonString(request))
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        Mockito.verify(transactionWrapper, Mockito.never()).create(Mockito.any(Propose.class));
    }

    private String asJsonString(NewProposeRequest request) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(request);
    }
}
