package fr.m2i.apichat.repository;

import fr.m2i.apichat.domain.Canal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CanalRepositoryWithBagRelationshipsImpl implements CanalRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Canal> fetchBagRelationships(Optional<Canal> canal) {
        return canal.map(this::fetchUsers);
    }

    @Override
    public Page<Canal> fetchBagRelationships(Page<Canal> canals) {
        return new PageImpl<>(fetchBagRelationships(canals.getContent()), canals.getPageable(), canals.getTotalElements());
    }

    @Override
    public List<Canal> fetchBagRelationships(List<Canal> canals) {
        return Optional.of(canals).map(this::fetchUsers).orElse(Collections.emptyList());
    }

    Canal fetchUsers(Canal result) {
        return entityManager
            .createQuery("select canal from Canal canal left join fetch canal.users where canal is :canal", Canal.class)
            .setParameter("canal", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Canal> fetchUsers(List<Canal> canals) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, canals.size()).forEach(index -> order.put(canals.get(index).getId(), index));
        List<Canal> result = entityManager
            .createQuery("select distinct canal from Canal canal left join fetch canal.users where canal in :canals", Canal.class)
            .setParameter("canals", canals)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
