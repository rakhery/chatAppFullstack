package fr.m2i.apichat.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link fr.m2i.apichat.domain.Message} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageDTO implements Serializable {

    private Long id;

    @NotNull
    private String content;

    @NotNull
    private Instant createdAt;

    private Instant updateAt;

    private UserDTO user;

    private CanalDTO canal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CanalDTO getCanal() {
        return canal;
    }

    public void setCanal(CanalDTO canal) {
        this.canal = canal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageDTO)) {
            return false;
        }

        MessageDTO messageDTO = (MessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, messageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageDTO{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updateAt='" + getUpdateAt() + "'" +
            ", user=" + getUser() +
            ", canal=" + getCanal() +
            "}";
    }
}
