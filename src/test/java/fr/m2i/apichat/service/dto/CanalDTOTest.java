package fr.m2i.apichat.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import fr.m2i.apichat.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CanalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CanalDTO.class);
        CanalDTO canalDTO1 = new CanalDTO();
        canalDTO1.setId(1L);
        CanalDTO canalDTO2 = new CanalDTO();
        assertThat(canalDTO1).isNotEqualTo(canalDTO2);
        canalDTO2.setId(canalDTO1.getId());
        assertThat(canalDTO1).isEqualTo(canalDTO2);
        canalDTO2.setId(2L);
        assertThat(canalDTO1).isNotEqualTo(canalDTO2);
        canalDTO1.setId(null);
        assertThat(canalDTO1).isNotEqualTo(canalDTO2);
    }
}
