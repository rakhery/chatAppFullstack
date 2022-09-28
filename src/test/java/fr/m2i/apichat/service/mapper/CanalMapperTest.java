package fr.m2i.apichat.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CanalMapperTest {

    private CanalMapper canalMapper;

    @BeforeEach
    public void setUp() {
        canalMapper = new CanalMapperImpl();
    }
}
