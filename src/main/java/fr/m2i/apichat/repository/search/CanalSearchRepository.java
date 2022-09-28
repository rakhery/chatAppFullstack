package fr.m2i.apichat.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import fr.m2i.apichat.domain.Canal;
import fr.m2i.apichat.repository.CanalRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Canal} entity.
 */
public interface CanalSearchRepository extends ElasticsearchRepository<Canal, Long>, CanalSearchRepositoryInternal {}

interface CanalSearchRepositoryInternal {
    Stream<Canal> search(String query);

    Stream<Canal> search(Query query);

    void index(Canal entity);
}

class CanalSearchRepositoryInternalImpl implements CanalSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CanalRepository repository;

    CanalSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CanalRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Canal> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery);
    }

    @Override
    public Stream<Canal> search(Query query) {
        return elasticsearchTemplate.search(query, Canal.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Canal entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
