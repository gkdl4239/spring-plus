package org.example.expert.domain.user.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final ElasticsearchClient client;

    public List<Map<String, Object>> searchByNickname(String nickname) throws IOException {
        // Query 생성
        Query query = Query.of(q -> q
            .match(m -> m
                .field("nickname")  // 필드 이름
                .query(nickname)    // 검색어
            )
        );

        // SearchRequest 생성
        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index("users")  // 검색 대상 인덱스
            .query(query)    // 위에서 생성한 쿼리
        );

        SearchResponse<Object> response = client.search(searchRequest, Object.class);

        return response.hits().hits().stream()
            .map(Hit::source)
            .map(source -> (Map<String, Object>) source)
            .collect(Collectors.toList());
    }
}
