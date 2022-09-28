package fr.m2i.apichat.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * l'entité Canal
 */
@Entity
@Table(name = "canal")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "canal")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Canal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "canal")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "canal" }, allowSetters = true)
    private Set<Message> messages = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "rel_canal__user", joinColumns = @JoinColumn(name = "canal_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();


    public Long getId() {
        return this.id;
    }

    public Canal id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Canal name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Canal createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Canal updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return this.description;
    }

    public Canal description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(Set<Message> messages) {
        if (this.messages != null) {
            this.messages.forEach(i -> i.setCanal(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setCanal(this));
        }
        this.messages = messages;
    }

    public Canal messages(Set<Message> messages) {
        this.setMessages(messages);
        return this;
    }

    public Canal addMessages(Message message) {
        this.messages.add(message);
        message.setCanal(this);
        return this;
    }

    public Canal removeMessages(Message message) {
        this.messages.remove(message);
        message.setCanal(null);
        return this;
    }

    public Set<User> getUsers() {
        return this.users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Canal users(Set<User> users) {
        this.setUsers(users);
        return this;
    }

    public Canal addUser(User user) {
        this.users.add(user);
        return this;
    }

    public Canal removeUser(User user) {
        this.users.remove(user);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Canal)) {
            return false;
        }
        return id != null && id.equals(((Canal) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Canal{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
