package br.com.zup.bootcamp.proposta.resources;

import java.math.BigDecimal;
import java.util.Optional;

import org.hamcrest.CoreMatchers;
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

import br.com.zup.bootcamp.proposta.model.AnalysisStatus;
import br.com.zup.bootcamp.proposta.model.Card;
import br.com.zup.bootcamp.proposta.model.Propose;
import br.com.zup.bootcamp.proposta.repository.ProposeRepository;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProposeFollowUpController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProposeFollowUpControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProposeRepository proposeRepository;

    @Test
    @DisplayName("Find proposal that exists, not eligible")
    void findProposal() throws Exception {
        String proposeId = "123";

        Propose propose = new Propose("documento", "email@email", "nome", "endereco", BigDecimal.ONE);
        Mockito.when(proposeRepository.findById(proposeId)).thenReturn(Optional.of(propose));

        mvc.perform(
                MockMvcRequestBuilders
                        .get(getUrl(proposeId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.equalTo(propose.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialStatus", CoreMatchers.equalTo(propose.getFinancialAnalysisStatus().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cardId", CoreMatchers.nullValue()));

    }

    @Test
    @DisplayName("Find proposal that exists, eligible without card")
    void findProposalOkNoCard() throws Exception {
        String proposeId = "123";

        Propose propose = new Propose("documento", "email@email", "nome", "endereco", BigDecimal.ONE);
        propose.changeFinancialAnalysis(AnalysisStatus.ELIGIBLE);
        Mockito.when(proposeRepository.findById(proposeId)).thenReturn(Optional.of(propose));

        mvc.perform(
                MockMvcRequestBuilders
                        .get(getUrl(proposeId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.equalTo(propose.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialStatus", CoreMatchers.equalTo(propose.getFinancialAnalysisStatus().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cardId", CoreMatchers.nullValue()));

    }

    @Test
    @DisplayName("Find proposal that exists, eligible with card")
    void findProposalOkWithCard() throws Exception {
        String proposeId = "123";
        String cardId = "CARD-123";

        Propose propose = new Propose("documento", "email@email", "nome", "endereco", BigDecimal.ONE,
                AnalysisStatus.ELIGIBLE, null);
        Card card = new Card(cardId, propose, "titular");
        propose.setCard(card);

        Mockito.when(proposeRepository.findById(proposeId)).thenReturn(Optional.of(propose));

        mvc.perform(
                MockMvcRequestBuilders
                        .get(getUrl(proposeId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.equalTo(propose.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialStatus", CoreMatchers.equalTo(propose.getFinancialAnalysisStatus().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cardId", CoreMatchers.equalTo(cardId)));

    }

    @Test
    @DisplayName("Proposal not found")
    void proposalNotFound() throws Exception {
        String proposeId = "123";

        Mockito.when(proposeRepository.findById(proposeId)).thenReturn(Optional.empty());

        mvc.perform(
                MockMvcRequestBuilders
                        .get(getUrl(proposeId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    private String getUrl(String proposeId) {
        return "/api/propose/" + proposeId;
    }
}
