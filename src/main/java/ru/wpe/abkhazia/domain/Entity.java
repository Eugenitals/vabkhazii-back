package ru.wpe.abkhazia.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Entity extends Node {

    @Enumerated
    private EntityType type;

    @Transient
    protected Boolean leaf = true;

    @NotNull
    private float rate;

    private String phone;

    private String email;

    private String site;

    private String worktime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entity")
    private Set<GeoPoint> points = new HashSet<GeoPoint>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entity")
    private Set<Rewiev> rewievs = new HashSet<Rewiev>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "entity")
    private Set<Resource> resources = new HashSet<Resource>();

    @NotNull
    @ManyToOne(cascade = CascadeType.DETACH)
    private Region region;

    @JsonIgnore
    public Region getRegion() {
        return this.region;
    }

    @Override
    public String toString() {
        return this.getType() + " {" + this.getName() + " (" + this.getId() + ")}";
    }

    public static List<Entity> findAllActiveEntitys() {
        List<Entity> resultList = new ArrayList<Entity>();

        EntityManager em = Entity.entityManager();
        TypedQuery<Entity> q = em.createQuery("SELECT o FROM Entity o WHERE o.active = :active ORDER BY o.rate DESC, o.name ASC", Entity.class);
        q.setParameter("active", true);
        for (Entity entity : q.getResultList()) {
            Set<Resource> resList = new HashSet<Resource>();

            for (Resource res : entity.getResources()) {
                resList.add(Resource.findResource(res.getId()));
            }
            entity.setResources(resList);

            resultList.add(entity);
        }

        return resultList;
    }

    public static long countEntitys() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Entity o", Long.class).getSingleResult();
    }

    public static List<Entity> findAllEntitys() {
        return entityManager().createQuery("SELECT o FROM Entity o ORDER BY o.rate DESC, o.name ASC", Entity.class).getResultList();
    }

    public static List<Entity> findAllEntitys(String like) {
        if (like.length() == 0) {
            return findAllEntitys();
        } else {
            TypedQuery<Entity> q =  entityManager().createQuery("SELECT o FROM Entity o WHERE lower(o.name) LIKE :like", Entity.class);
            q.setParameter("like", "%" + like.toLowerCase() + "%");
            return q.getResultList();
        }
    }

    public static Entity findEntity(Long id) {
        if (id == null) return null;
        Entity result;
        result = entityManager().find(Entity.class, id);
        Set<Resource> resList = new HashSet<Resource>();
        for (Resource res : result.getResources()) {
            resList.add(Resource.findResource(res.getId()));
        }
        result.setResources(resList);
        return result;
    }

    public static List<Entity> findEntityEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Entity o ORDER BY o.rate DESC, o.name ASC", Entity.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
}
