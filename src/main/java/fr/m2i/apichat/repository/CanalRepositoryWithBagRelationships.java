package fr.m2i.apichat.repository;

import fr.m2i.apichat.domain.Canal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CanalRepositoryWithBagRelationships {
    Optional<Canal> fetchBagRelationships(Optional<Canal> canal);

    List<Canal> fetchBagRelationships(List<Canal> canals);

    Page<Canal> fetchBagRelationships(Page<Canal> canals);
}
