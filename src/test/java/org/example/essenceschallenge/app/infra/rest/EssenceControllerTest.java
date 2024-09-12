package org.example.essenceschallenge.app.infra.rest;

import org.example.essenceschallenge.app.infra.config.AuthFilter;
import org.example.essenceschallenge.app.service.AuthService;
import org.example.essenceschallenge.app.service.EssenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EssenceController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {AuthFilter.class, AuthService.class}))
@AutoConfigureMockMvc(addFilters = false)
public class EssenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EssenceService essenceService;

    @Test
    void mustReturnAllEssencesWhenRequestIsSuccessful() throws Exception {
        List<EssenceResponse> essences = Arrays.asList(
                new EssenceResponse("1", "Essence 1", Arrays.asList("Value 1", "Value 2")),
                new EssenceResponse("2", "Essence 2", Arrays.asList("Value 3", "Value 4"))
        );
        when(essenceService.getAllEssences()).thenReturn(essences);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/essences")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("Essence 1")))
                .andExpect(jsonPath("$[0].values", hasSize(2)));
    }

    @Test
    void mustReturnEssenceByIdWhenRequestIsSuccessful() throws Exception {
        EssenceResponse essence = new EssenceResponse("1", "Essence 1", Arrays.asList("Value 1", "Value 2"));
        when(essenceService.getEssenceById("1")).thenReturn(essence);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/essences/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Essence 1")))
                .andExpect(jsonPath("$.values", hasSize(2)));
    }

    @Test
    void mustThrowHttpErrorWhenEssenceDoesNotExist() throws Exception {
        when(essenceService.getEssenceById("non-existent"))
                .thenThrow(new HttpException("Not found", HttpStatus.NOT_FOUND.value()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/essences/non-existent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

