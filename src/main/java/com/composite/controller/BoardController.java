package com.composite.controller;

import com.composite.controller.BoardResponse.GetList;
import com.composite.service.BoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;


    @PostMapping
    public ResponseEntity<Void> create(@RequestBody BoardRequest.Create request) {
        boardService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse.GetList>> getAll() {
        List<GetList> boards = boardService.getAll();
        return ResponseEntity.ok(boards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse.Get> getById(@PathVariable Long id) {
        BoardResponse.Get board = boardService.getById(id);
        return ResponseEntity.ok(board);
    }


}
