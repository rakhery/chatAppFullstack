package fr.m2i.apichat.service;

import fr.m2i.apichat.service.dto.CanalDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link fr.m2i.apichat.domain.Canal}.
 */
public interface CanalService {
    /**
     * Save a canal.
     *
     * @param canalDTO the entity to save.
     * @return the persisted entity.
     */
    CanalDTO save(CanalDTO canalDTO);

    /**
     * Updates a canal.
     *
     * @param canalDTO the entity to update.
     * @return the persisted entity.
     */
    CanalDTO update(CanalDTO canalDTO);

    /**
     * Partially updates a canal.
     *
     * @param canalDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CanalDTO> partialUpdate(CanalDTO canalDTO);

    /**
     * Get all the canals.
     *
     * @return the list of entities.
     */
    List<CanalDTO> findAll();

    /**
     * Get all the canals with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CanalDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" canal.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CanalDTO> findOne(Long id);

    /**
     * Delete the "id" canal.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the canal corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<CanalDTO> search(String query);
}
