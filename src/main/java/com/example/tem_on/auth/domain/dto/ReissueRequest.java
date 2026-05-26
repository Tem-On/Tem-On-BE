package com.example.tem_on.auth.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueRequest {

    private String refreshToken;
}