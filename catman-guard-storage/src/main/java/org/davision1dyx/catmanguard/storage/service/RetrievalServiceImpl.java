package org.davision1dyx.catmanguard.storage.service;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.RetrievalDTO;
import org.davision1dyx.catmanguard.api.storage.service.RetrievalService;
import org.davision1dyx.catmanguard.api.storage.vo.RetrievalVO;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Davison
 * @date 2026-05-05
 * @description
 */
@Slf4j
@Service
public class RetrievalServiceImpl implements RetrievalService {

    private final VectorStore vectorStore;

    public RetrievalServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public RetrievalVO retrieve(RetrievalDTO retrievalDTO) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(retrievalDTO.getQuery())
                .topK(retrievalDTO.getTopN())
                .similarityThreshold(retrievalDTO.getSimilarity())
//                .filterExpression()
                .build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        String documentContent = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n=========文档分隔线===========\n\n"));
        return RetrievalVO.builder()
                .content(documentContent)
                .build();
    }
}
