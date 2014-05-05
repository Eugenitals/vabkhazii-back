package ru.wpe.abkhazia.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Resource {

    @Enumerated
    private ResourceType type;

    @Size(max = 50)
    private String filename;

    @Size(max = 500)
    private String description;

    private String contentType;

    private long sizee;

    @NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    @Transient
    @Size(max = 100)
    private String url;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Entity entity;

    @JsonIgnore
    public String getUrl() {
        return this.url;
    }

    @JsonIgnore
    public byte[] getContent() {
        return this.content;
    }

    @JsonIgnore
    public Entity getEntity() {
        return this.entity;
    }

    public static List<Resource> findResourceEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Resource o", Resource.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static Resource findResource(Long id) {
        if (id == null) return null;
        return entityManager().find(Resource.class, id);
    }

    public static List<Resource> findAllResources() {
        return entityManager().createQuery("SELECT o FROM Resource o", Resource.class).getResultList();
    }

    public static long countResources() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Resource o", Long.class).getSingleResult();
    }

    public Resource() {}

    public Resource (Builder builder) {
        type = builder.type;
        filename = builder.filename;
        description = builder.description;
        contentType = builder.contentType;
        sizee = builder.sizee;
        content = builder.content;
        url = builder.url;
        entity = builder.entity;
    }

    public static class Builder {
        private String filename;
        private String contentType;
        private long sizee;
        private byte[] content;
        private Entity entity;

        private ResourceType type = ResourceType.IMAGE;
        private String description = "";
        private String url = "";

        public Resource build() {
            return new Resource(this);
        }

        public Builder filename(String _val) {
            filename = _val;
            return this;
        }

        public Builder contentType(String _val) {
            contentType = _val;
            return this;
        }

        public Builder sizee(long _val) {
            sizee = _val;
            return this;
        }

        public Builder content(byte [] _val) {
            content = _val;
            return this;
        }

        public Builder entity(Entity _val) {
            entity = _val;
            return this;
        }

        public Builder type(ResourceType _val) {
            type = _val;
            return this;
        }

        public Builder description(String _val) {
            description = _val;
            return this;
        }

        public Builder url(String _val) {
            url = _val;
            return this;
        }
    }
}
