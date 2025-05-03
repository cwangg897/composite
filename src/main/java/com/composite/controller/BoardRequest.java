package com.composite.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class BoardRequest {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Create{
        private String content;
    }

}
