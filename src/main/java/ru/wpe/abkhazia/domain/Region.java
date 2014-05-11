package ru.wpe.abkhazia.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Region {

    @NotNull
    private Boolean active = true;

    @NotNull
    private String name;

    private String weatherChanel;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "region")
    private Set<GeoPoint> points = new HashSet<GeoPoint>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "region")
    private Set<Entity> entities = new HashSet<Entity>();

    @JsonIgnore
    public Set<ru.wpe.abkhazia.domain.GeoPoint> getPoints() {
        return this.points;
    }

    @JsonIgnore
    public Set<ru.wpe.abkhazia.domain.Entity> getEntities() {
        return this.entities;
    }

    public static List<Region> findAllRegions() {
        return entityManager().createQuery("SELECT o FROM Region o", Region.class).getResultList();
    }

    public static List<Region> findAllRegions(String like) {
        if (like.length() == 0) {
            return findAllRegions();
        } else {
            TypedQuery<Region> q =  entityManager().createQuery("SELECT o FROM Region o WHERE lower(o.name) LIKE :like", Region.class);
            q.setParameter("like", "%" + like.toLowerCase() + "%");
            return q.getResultList();
        }
    }

    public static List<ru.wpe.abkhazia.domain.Region> findAllActiveRegions() {
        EntityManager em = Entity.entityManager();
        TypedQuery<Region> q = em.createQuery("SELECT o FROM Region o WHERE o.active = :active ORDER BY o.name ASC", Region.class);
        q.setParameter("active", true);
        return q.getResultList();
    }
}
