package org.example.essenceschallenge.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.essenceschallenge.app.infra.rest.EssenceResponse;
import org.example.essenceschallenge.app.infra.rest.HttpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class EssenceService {

    private static final String BASE_URL = "https://api.dev.grupoboticario.com.br/v1/essences-challenge/essences";

    @Value("${third.party.token}")
    private String thirdPartyToken;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EssenceService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Cacheable(value = "essences", key = "'all'", unless = "#result == null")
    public List<EssenceResponse> getAllEssences() throws IOException {
        HttpRequest request = createGetRequest(BASE_URL);
        return attemptGetRequest(request, new TypeReference<>() {});
    }

    @Cacheable(value = "essencesById", key = "#id", unless = "#result == null")
    public EssenceResponse getEssenceById(String id) {
        String getEssenceByIdURL = BASE_URL + "/" + id;
        HttpRequest request = createGetRequest(getEssenceByIdURL);
        return attemptGetRequest(request, new TypeReference<>() {});
    }

    private HttpRequest createGetRequest(String url) {
        String auth = "Basic " + thirdPartyToken;
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", auth)
                .GET()
                .build();
    }

    private <T> T attemptGetRequest(HttpRequest request, TypeReference<T> typeReference) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (!isSuccessful(response.statusCode())) {
                throw new HttpException("HTTP error: " + response.body(), response.statusCode());
            }
            return objectMapper.readValue(response.body(), typeReference);
        } catch(IOException | InterruptedException e) {
            throw new RuntimeException("Request failed", e);
        }
    }

    private boolean isSuccessful(int statusCode) {
        return statusCode >= 200  && statusCode <= 299;
    }
}
