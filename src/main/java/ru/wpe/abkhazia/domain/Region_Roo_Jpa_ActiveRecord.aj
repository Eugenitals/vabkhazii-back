// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.wpe.abkhazia.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import ru.wpe.abkhazia.domain.Region;

privileged aspect Region_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Region.entityManager;
    
    public static final EntityManager Region.entityManager() {
        EntityManager em = new Region().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Region.countRegions() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Region o", Long.class).getSingleResult();
    }
    
    public static List<Region> Region.findAllRegions() {
        return entityManager().createQuery("SELECT o FROM Region o", Region.class).getResultList();
    }
    
    public static Region Region.findRegion(Long id) {
        if (id == null) return null;
        return entityManager().find(Region.class, id);
    }
    
    public static List<Region> Region.findRegionEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Region o", Region.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Region.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Region.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Region attached = Region.findRegion(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Region.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Region.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Region Region.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Region merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
