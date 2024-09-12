package org.example.essenceschallenge.app.infra.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EssenceResponse(String id, String name, List<String> values) {}
