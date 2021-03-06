// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.wpe.abkhazia.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import ru.wpe.abkhazia.domain.GeoPoint;

privileged aspect GeoPoint_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager GeoPoint.entityManager;
    
    public static final EntityManager GeoPoint.entityManager() {
        EntityManager em = new GeoPoint().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long GeoPoint.countGeoPoints() {
        return entityManager().createQuery("SELECT COUNT(o) FROM GeoPoint o", Long.class).getSingleResult();
    }
    
    public static GeoPoint GeoPoint.findGeoPoint(Long id) {
        if (id == null) return null;
        return entityManager().find(GeoPoint.class, id);
    }
    
    public static List<GeoPoint> GeoPoint.findGeoPointEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM GeoPoint o", GeoPoint.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void GeoPoint.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void GeoPoint.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            GeoPoint attached = GeoPoint.findGeoPoint(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void GeoPoint.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void GeoPoint.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public GeoPoint GeoPoint.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        GeoPoint merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
