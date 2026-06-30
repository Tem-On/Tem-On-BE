package com.example.temon.commerceservice.global.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiUserPrincipal {

    private final Long userId;
    private final String role;
}