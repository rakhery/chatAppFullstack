package fr.m2i.apichat.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import fr.m2i.apichat.domain.Canal;
import fr.m2i.apichat.repository.CanalRepository;
import fr.m2i.apichat.repository.search.CanalSearchRepository;
import fr.m2i.apichat.service.CanalService;
import fr.m2i.apichat.service.dto.CanalDTO;
import fr.m2i.apichat.service.mapper.CanalMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Canal}.
 */
@Service
@Transactional
public class CanalServiceImpl implements CanalService {

    private final Logger log = LoggerFactory.getLogger(CanalServiceImpl.class);

    private final CanalRepository canalRepository;

    private final CanalMapper canalMapper;

    private final CanalSearchRepository canalSearchRepository;

    public CanalServiceImpl(CanalRepository canalRepository, CanalMapper canalMapper, CanalSearchRepository canalSearchRepository) {
        this.canalRepository = canalRepository;
        this.canalMapper = canalMapper;
        this.canalSearchRepository = canalSearchRepository;
    }

    @Override
    public CanalDTO save(CanalDTO canalDTO) {
        log.debug("Request to save Canal : {}", canalDTO);
        Canal canal = canalMapper.toEntity(canalDTO);
        canal = canalRepository.save(canal);
        CanalDTO result = canalMapper.toDto(canal);
        canalSearchRepository.index(canal);
        return result;
    }

    @Override
    public CanalDTO update(CanalDTO canalDTO) {
        log.debug("Request to update Canal : {}", canalDTO);
        Canal canal = canalMapper.toEntity(canalDTO);
        canal = canalRepository.save(canal);
        CanalDTO result = canalMapper.toDto(canal);
        canalSearchRepository.index(canal);
        return result;
    }

    @Override
    public Optional<CanalDTO> partialUpdate(CanalDTO canalDTO) {
        log.debug("Request to partially update Canal : {}", canalDTO);

        return canalRepository
            .findById(canalDTO.getId())
            .map(existingCanal -> {
                canalMapper.partialUpdate(existingCanal, canalDTO);

                return existingCanal;
            })
            .map(canalRepository::save)
            .map(savedCanal -> {
                canalSearchRepository.save(savedCanal);

                return savedCanal;
            })
            .map(canalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanalDTO> findAll() {
        log.debug("Request to get all Canals");
        return canalRepository.findAll().stream().map(canalMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<CanalDTO> findAllWithEagerRelationships(Pageable pageable) {
        return canalRepository.findAllWithEagerRelationships(pageable).map(canalMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CanalDTO> findOne(Long id) {
        log.debug("Request to get Canal : {}", id);
        return canalRepository.findOneWithEagerRelationships(id).map(canalMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Canal : {}", id);
        canalRepository.deleteById(id);
        canalSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanalDTO> search(String query) {
        log.debug("Request to search Canals for query {}", query);
        return StreamSupport
            .stream(canalSearchRepository.search(query).spliterator(), false)
            .map(canalMapper::toDto)
            .collect(Collectors.toList());
    }
}
