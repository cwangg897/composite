package com.composite.service;

import com.composite.controller.BoardRequest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    BoardService boardService;
    private final AtomicInteger counter = new AtomicInteger(1); // 번호를 쌓을 카운터
    @DisplayName("게시판데이터_생성")
    @Test
    public void 게시판데이터_생성() throws Exception {
        int threadCount = 10_000;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    int num = counter.getAndIncrement();
                    String content = "게시판 데이터" + num;
                    BoardRequest.Create request = new BoardRequest.Create(content);
                    boardService.create(request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown(); // ⭐️ 중요!
                }
            });
        }

        latch.await(); // 모든 쓰레드 작업이 끝날 때까지 기다림
        executor.shutdown();
        System.out.println("🌟 게시판 데이터 10,000개 생성 완료!");
    }

}
