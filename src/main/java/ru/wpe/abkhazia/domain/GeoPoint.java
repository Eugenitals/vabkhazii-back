package ru.wpe.abkhazia.domain;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class GeoPoint {

    @NotNull
    private Boolean active = true;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    private String address;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Region region;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Entity entity;

    @JsonIgnore
    public Region getRegion() {
        return this.region;
    }

    @JsonIgnore
    public Entity getEntity() {
        return this.entity;
    }

    public static List<GeoPoint> findAllActiveGeoPoints() {
        EntityManager em = Entity.entityManager();
        TypedQuery<GeoPoint> q = em.createQuery("SELECT o FROM GeoPoint o WHERE o.active = :active", GeoPoint.class);
        q.setParameter("active", true);
        return q.getResultList();
    }

    public static List<GeoPoint> findAllGeoPoints() {
        return entityManager().createQuery("SELECT o FROM GeoPoint o", GeoPoint.class).getResultList();
    }

    public static List<GeoPoint> findAllGeoPoints(String like) {
        if (like.length() == 0) {
            return findAllGeoPoints();
        } else {
            Set<Entity> entities = new HashSet<Entity>();
            TypedQuery<Entity> qe =  entityManager().createQuery("SELECT o FROM Entity o WHERE lower(o.name) LIKE :like", Entity.class);
            qe.setParameter("like", "%" + like.toLowerCase() + "%");
            entities.addAll(qe.getResultList());

            if (entities.size() > 0) {
                TypedQuery<GeoPoint> qgp =  entityManager().createQuery("SELECT o FROM GeoPoint o WHERE o.entity IN :entities", GeoPoint.class);
                qgp.setParameter("entities", entities);
                return qgp.getResultList();
            }

            return new ArrayList<GeoPoint>();
        }
    }

    @Override
    public String toString() {
        return "Гео-точка [" +
                "адресс='" + this.address + '\'' +
                ", активность=" + this.active +
                ']';
    }

    public GeoPoint() {
    }

    public GeoPoint(Builder builder) {
        active = builder.active;
        latitude = builder.latitude;
        longitude = builder.longitude;
        address = builder.address;
        region = builder.region;
        entity = builder.entity;
    }

    public static class Builder {
        private Boolean active = true;
        private double latitude;
        private double longitude;
        private String address;
        private Region region;
        private Entity entity;

        public GeoPoint build() {
            return new GeoPoint(this);
        }

        public Builder active(boolean _active) {
            active = _active;
            return this;
        }

        public Builder latitude(double _latitude) {
            latitude = _latitude;
            return this;
        }

        public Builder longitude(double _longitude) {
            longitude = _longitude;
            return this;
        }

        public Builder address(String _address) {
            address = _address;
            return this;
        }

        public Builder region(Region _region) {
            region = _region;
            return this;
        }

        public Builder entity(Entity _entity) {
            entity = _entity;
            return this;
        }
    }
}
