package com.example.petshop.web;

import io.helidon.common.Api;
import io.helidon.json.binding.Json;
import io.helidon.validation.Validation;

/** The body of a POST: checked by Helidon Validation before the endpoint sees it. */
@SuppressWarnings(Api.SUPPRESS_PREVIEW) // Helidon JSON binding is a preview API in Helidon 27
@Validation.Validated
@Json.Entity
public record NewPet(
        @Validation.String.NotBlank @Validation.String.Length(80) String name,
        @Validation.String.NotBlank @Validation.String.Length(40) String species) {}
