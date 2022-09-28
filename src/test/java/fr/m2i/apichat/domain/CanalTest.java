package fr.m2i.apichat.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.m2i.apichat.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CanalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Canal.class);
        Canal canal1 = new Canal();
        canal1.setId(1L);
        Canal canal2 = new Canal();
        canal2.setId(canal1.getId());
        assertThat(canal1).isEqualTo(canal2);
        canal2.setId(2L);
        assertThat(canal1).isNotEqualTo(canal2);
        canal1.setId(null);
        assertThat(canal1).isNotEqualTo(canal2);
    }
}
