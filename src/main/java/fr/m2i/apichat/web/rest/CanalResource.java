package fr.m2i.apichat.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import fr.m2i.apichat.repository.CanalRepository;
import fr.m2i.apichat.service.CanalService;
import fr.m2i.apichat.service.dto.CanalDTO;
import fr.m2i.apichat.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link fr.m2i.apichat.domain.Canal}.
 */
@RestController
@RequestMapping("/api")
public class CanalResource {

    private final Logger log = LoggerFactory.getLogger(CanalResource.class);

    private static final String ENTITY_NAME = "canal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CanalService canalService;

    private final CanalRepository canalRepository;

    public CanalResource(CanalService canalService, CanalRepository canalRepository) {
        this.canalService = canalService;
        this.canalRepository = canalRepository;
    }

    /**
     * {@code POST  /canals} : Create a new canal.
     *
     * @param canalDTO the canalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new canalDTO, or with status {@code 400 (Bad Request)} if the canal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/canals")
    public ResponseEntity<CanalDTO> createCanal(@Valid @RequestBody CanalDTO canalDTO) throws URISyntaxException {
        log.debug("REST request to save Canal : {}", canalDTO);
        if (canalDTO.getId() != null) {
            throw new BadRequestAlertException("A new canal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CanalDTO result = canalService.save(canalDTO);
        return ResponseEntity
            .created(new URI("/api/canals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /canals/:id} : Updates an existing canal.
     *
     * @param id the id of the canalDTO to save.
     * @param canalDTO the canalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated canalDTO,
     * or with status {@code 400 (Bad Request)} if the canalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the canalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/canals/{id}")
    public ResponseEntity<CanalDTO> updateCanal(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CanalDTO canalDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Canal : {}, {}", id, canalDTO);
        if (canalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, canalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!canalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CanalDTO result = canalService.update(canalDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, canalDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /canals/:id} : Partial updates given fields of an existing canal, field will ignore if it is null
     *
     * @param id the id of the canalDTO to save.
     * @param canalDTO the canalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated canalDTO,
     * or with status {@code 400 (Bad Request)} if the canalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the canalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the canalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/canals/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CanalDTO> partialUpdateCanal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CanalDTO canalDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Canal partially : {}, {}", id, canalDTO);
        if (canalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, canalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!canalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CanalDTO> result = canalService.partialUpdate(canalDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, canalDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /canals} : get all the canals.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of canals in body.
     */
    @GetMapping("/canals")
    public List<CanalDTO> getAllCanals(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Canals");
        return canalService.findAll();
    }

    /**
     * {@code GET  /canals/:id} : get the "id" canal.
     *
     * @param id the id of the canalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the canalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/canals/{id}")
    public ResponseEntity<CanalDTO> getCanal(@PathVariable Long id) {
        log.debug("REST request to get Canal : {}", id);
        Optional<CanalDTO> canalDTO = canalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(canalDTO);
    }

    /**
     * {@code DELETE  /canals/:id} : delete the "id" canal.
     *
     * @param id the id of the canalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/canals/{id}")
    public ResponseEntity<Void> deleteCanal(@PathVariable Long id) {
        log.debug("REST request to delete Canal : {}", id);
        canalService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/canals?query=:query} : search for the canal corresponding
     * to the query.
     *
     * @param query the query of the canal search.
     * @return the result of the search.
     */
    @GetMapping("/_search/canals")
    public List<CanalDTO> searchCanals(@RequestParam String query) {
        log.debug("REST request to search Canals for query {}", query);
        return canalService.search(query);
    }
}
