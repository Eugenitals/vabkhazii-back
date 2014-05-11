package ru.wpe.abkhazia.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

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
        return allQueryWithoutContent().setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static Resource findResource(Long id) {
        if (id == null) return null;
        return entityManager().find(Resource.class, id);
    }

    public static List<Resource> findAllResources() {
        return allQueryWithoutContent().getResultList();
    }

    private static Query allQueryWithoutContent() {
        CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(Resource.class);
        cq.select(cb.construct(Resource.class,
                e.get("id"),
                e.get("version"),
                e.get("type"),
                e.get("filename"),
                e.get("description"),
                e.get("contentType"),
                e.get("sizee"),
                e.get("entity")));
        return entityManager().createQuery(cq);
    }

    public static List<Resource> findAllResources(String like) {
        if (like.length() == 0) {
            return findAllResources();
        } else {
            CriteriaBuilder cb = entityManager().getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery();
            Root e = cq.from(Resource.class);
            cq.select(cb.construct(Resource.class,
                    e.get("id"),
                    e.get("version"),
                    e.get("type"),
                    e.get("filename"),
                    e.get("description"),
                    e.get("contentType"),
                    e.get("sizee"),
                    e.get("entity")))
                    .where(cb.or(
                            cb.like(cb.lower(e.get("description")), "%" + like.toLowerCase() + "%"),
                            cb.like(cb.lower(e.get("filename")), "%" + like.toLowerCase() + "%")
                    ));
            return entityManager().createQuery(cq).getResultList();
        }
    }

    public static long countResources() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Resource o", Long.class).getSingleResult();
    }

    public Resource() {
    }

    public Resource(String description) {
        this.description = description;
    }

    public Resource(Long id,
                    Integer version,
                    ResourceType type,
                    String filename,
                    String description,
                    String contentType,
                    long sizee,
                    Entity entity) {
        this.setId(id);
        this.setVersion(version);
        this.type = type;
        this.filename = filename;
        this.description = description;
        this.contentType = contentType;
        this.sizee = sizee;
        this.entity = entity;
    }

    public Resource(Builder builder) {
        this.type = builder.type;
        this.filename = builder.filename;
        this.description = builder.description;
        this.contentType = builder.contentType;
        this.sizee = builder.sizee;
        this.content = builder.content;
        this.url = builder.url;
        this.entity = builder.entity;
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

        public Builder content(byte[] _val) {
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
