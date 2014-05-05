package ru.wpe.abkhazia.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

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

    public static List<ru.wpe.abkhazia.domain.Region> findAllActiveRegions() {
        EntityManager em = Entity.entityManager();
        TypedQuery<Region> q = em.createQuery("SELECT o FROM Region o WHERE o.active = :active ORDER BY o.name ASC", Region.class);
        q.setParameter("active", true);
        return q.getResultList();
    }
}
