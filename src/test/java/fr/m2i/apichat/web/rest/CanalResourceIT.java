package fr.m2i.apichat.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.m2i.apichat.IntegrationTest;
import fr.m2i.apichat.domain.Canal;
import fr.m2i.apichat.repository.CanalRepository;
import fr.m2i.apichat.repository.search.CanalSearchRepository;
import fr.m2i.apichat.service.CanalService;
import fr.m2i.apichat.service.dto.CanalDTO;
import fr.m2i.apichat.service.mapper.CanalMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CanalResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CanalResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/canals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/canals";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CanalRepository canalRepository;

    @Mock
    private CanalRepository canalRepositoryMock;

    @Autowired
    private CanalMapper canalMapper;

    @Mock
    private CanalService canalServiceMock;

    @Autowired
    private CanalSearchRepository canalSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCanalMockMvc;

    private Canal canal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Canal createEntity(EntityManager em) {
        Canal canal = new Canal()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .description(DEFAULT_DESCRIPTION);
        return canal;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Canal createUpdatedEntity(EntityManager em) {
        Canal canal = new Canal()
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .description(UPDATED_DESCRIPTION);
        return canal;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        canalSearchRepository.deleteAll();
        assertThat(canalSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        canal = createEntity(em);
    }

    @Test
    @Transactional
    void createCanal() throws Exception {
        int databaseSizeBeforeCreate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        // Create the Canal
        CanalDTO canalDTO = canalMapper.toDto(canal);
        restCanalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(canalDTO)))
            .andExpect(status().isCreated());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Canal testCanal = canalList.get(canalList.size() - 1);
        assertThat(testCanal.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCanal.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCanal.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testCanal.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createCanalWithExistingId() throws Exception {
        // Create the Canal with an existing ID
        canal.setId(1L);
        CanalDTO canalDTO = canalMapper.toDto(canal);

        int databaseSizeBeforeCreate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCanalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(canalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        // set the field null
        canal.setName(null);

        // Create the Canal, which fails.
        CanalDTO canalDTO = canalMapper.toDto(canal);

        restCanalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(canalDTO)))
            .andExpect(status().isBadRequest());

        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        // set the field null
        canal.setCreatedAt(null);

        // Create the Canal, which fails.
        CanalDTO canalDTO = canalMapper.toDto(canal);

        restCanalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(canalDTO)))
            .andExpect(status().isBadRequest());

        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCanals() throws Exception {
        // Initialize the database
        canalRepository.saveAndFlush(canal);

        // Get all the canalList
        restCanalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(canal.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCanalsWithEagerRelationshipsIsEnabled() throws Exception {
        when(canalServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCanalMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(canalServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCanalsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(canalServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCanalMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(canalRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCanal() throws Exception {
        // Initialize the database
        canalRepository.saveAndFlush(canal);

        // Get the canal
        restCanalMockMvc
            .perform(get(ENTITY_API_URL_ID, canal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(canal.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingCanal() throws Exception {
        // Get the canal
        restCanalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCanal() throws Exception {
        // Initialize the database
        canalRepository.saveAndFlush(canal);

        int databaseSizeBeforeUpdate = canalRepository.findAll().size();
        canalSearchRepository.save(canal);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());

        // Update the canal
        Canal updatedCanal = canalRepository.findById(canal.getId()).get();
        // Disconnect from session so that the updates on updatedCanal are not directly saved in db
        em.detach(updatedCanal);
        updatedCanal.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT).description(UPDATED_DESCRIPTION);
        CanalDTO canalDTO = canalMapper.toDto(updatedCanal);

        restCanalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, canalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(canalDTO))
            )
            .andExpect(status().isOk());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        Canal testCanal = canalList.get(canalList.size() - 1);
        assertThat(testCanal.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCanal.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCanal.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testCanal.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Canal> canalSearchList = IterableUtils.toList(canalSearchRepository.findAll());
                Canal testCanalSearch = canalSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCanalSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testCanalSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testCanalSearch.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
                assertThat(testCanalSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingCanal() throws Exception {
        int databaseSizeBeforeUpdate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        canal.setId(count.incrementAndGet());

        // Create the Canal
        CanalDTO canalDTO = canalMapper.toDto(canal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCanalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, canalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(canalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCanal() throws Exception {
        int databaseSizeBeforeUpdate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        canal.setId(count.incrementAndGet());

        // Create the Canal
        CanalDTO canalDTO = canalMapper.toDto(canal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCanalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(canalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCanal() throws Exception {
        int databaseSizeBeforeUpdate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        canal.setId(count.incrementAndGet());

        // Create the Canal
        CanalDTO canalDTO = canalMapper.toDto(canal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCanalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(canalDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCanalWithPatch() throws Exception {
        // Initialize the database
        canalRepository.saveAndFlush(canal);

        int databaseSizeBeforeUpdate = canalRepository.findAll().size();

        // Update the canal using partial update
        Canal partialUpdatedCanal = new Canal();
        partialUpdatedCanal.setId(canal.getId());

        partialUpdatedCanal.updatedAt(UPDATED_UPDATED_AT);

        restCanalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCanal.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCanal))
            )
            .andExpect(status().isOk());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        Canal testCanal = canalList.get(canalList.size() - 1);
        assertThat(testCanal.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCanal.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCanal.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testCanal.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateCanalWithPatch() throws Exception {
        // Initialize the database
        canalRepository.saveAndFlush(canal);

        int databaseSizeBeforeUpdate = canalRepository.findAll().size();

        // Update the canal using partial update
        Canal partialUpdatedCanal = new Canal();
        partialUpdatedCanal.setId(canal.getId());

        partialUpdatedCanal.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT).description(UPDATED_DESCRIPTION);

        restCanalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCanal.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCanal))
            )
            .andExpect(status().isOk());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        Canal testCanal = canalList.get(canalList.size() - 1);
        assertThat(testCanal.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCanal.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCanal.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testCanal.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingCanal() throws Exception {
        int databaseSizeBeforeUpdate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        canal.setId(count.incrementAndGet());

        // Create the Canal
        CanalDTO canalDTO = canalMapper.toDto(canal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCanalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, canalDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(canalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCanal() throws Exception {
        int databaseSizeBeforeUpdate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        canal.setId(count.incrementAndGet());

        // Create the Canal
        CanalDTO canalDTO = canalMapper.toDto(canal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCanalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(canalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCanal() throws Exception {
        int databaseSizeBeforeUpdate = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        canal.setId(count.incrementAndGet());

        // Create the Canal
        CanalDTO canalDTO = canalMapper.toDto(canal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCanalMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(canalDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Canal in the database
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCanal() throws Exception {
        // Initialize the database
        canalRepository.saveAndFlush(canal);
        canalRepository.save(canal);
        canalSearchRepository.save(canal);

        int databaseSizeBeforeDelete = canalRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the canal
        restCanalMockMvc
            .perform(delete(ENTITY_API_URL_ID, canal.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Canal> canalList = canalRepository.findAll();
        assertThat(canalList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(canalSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCanal() throws Exception {
        // Initialize the database
        canal = canalRepository.saveAndFlush(canal);
        canalSearchRepository.save(canal);

        // Search the canal
        restCanalMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + canal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(canal.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
