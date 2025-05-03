package com.composite.controller;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BoardResponse {


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Get{
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

    // 게시판 목록 조회 응답
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetList{
        private Long id;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
