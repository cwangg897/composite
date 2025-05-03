package com.composite.service;

import com.composite.controller.BoardRequest;
import com.composite.controller.BoardResponse;
import com.composite.controller.BoardResponse.GetList;
import com.composite.infrastructure.BoardEntity;
import com.composite.infrastructure.BoardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;


    @Transactional
    public void create(BoardRequest.Create request) {
        BoardEntity board = new BoardEntity(request.getContent());
        boardRepository.save(board);
    }

    @Cacheable(cacheNames = "board", key="all")
    public List<GetList> getAll(){
        List<BoardEntity> boards = boardRepository.findAll();
        return boards.stream()
            .map(board -> new GetList(board.getId(), board.getContent(), board.getCreatedAt(), board.getUpdatedAt()))
            .toList();
    }

    @Cacheable(cacheNames = "board", key = "#id")
    public BoardResponse.Get getById(Long id){
        BoardEntity board = boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return new BoardResponse.Get(board.getId(), board.getContent(), board.getCreatedAt(), board.getUpdatedAt());
    }



}
