// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.wpe.abkhazia.domain;

import java.util.Set;
import ru.wpe.abkhazia.domain.Entity;
import ru.wpe.abkhazia.domain.EntityType;
import ru.wpe.abkhazia.domain.GeoPoint;
import ru.wpe.abkhazia.domain.Region;
import ru.wpe.abkhazia.domain.Resource;
import ru.wpe.abkhazia.domain.Rewiev;

privileged aspect Entity_Roo_JavaBean {
    
    public EntityType Entity.getType() {
        return this.type;
    }
    
    public void Entity.setType(EntityType type) {
        this.type = type;
    }
    
    public Boolean Entity.getLeaf() {
        return this.leaf;
    }
    
    public void Entity.setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }
    
    public float Entity.getRate() {
        return this.rate;
    }
    
    public void Entity.setRate(float rate) {
        this.rate = rate;
    }
    
    public String Entity.getPhone() {
        return this.phone;
    }
    
    public void Entity.setPhone(String phone) {
        this.phone = phone;
    }
    
    public String Entity.getEmail() {
        return this.email;
    }
    
    public void Entity.setEmail(String email) {
        this.email = email;
    }
    
    public String Entity.getSite() {
        return this.site;
    }
    
    public void Entity.setSite(String site) {
        this.site = site;
    }
    
    public String Entity.getWorktime() {
        return this.worktime;
    }
    
    public void Entity.setWorktime(String worktime) {
        this.worktime = worktime;
    }
    
    public Set<GeoPoint> Entity.getPoints() {
        return this.points;
    }
    
    public void Entity.setPoints(Set<GeoPoint> points) {
        this.points = points;
    }
    
    public Set<Rewiev> Entity.getRewievs() {
        return this.rewievs;
    }
    
    public void Entity.setRewievs(Set<Rewiev> rewievs) {
        this.rewievs = rewievs;
    }
    
    public Set<Resource> Entity.getResources() {
        return this.resources;
    }
    
    public void Entity.setResources(Set<Resource> resources) {
        this.resources = resources;
    }
    
    public void Entity.setRegion(Region region) {
        this.region = region;
    }
    
}
