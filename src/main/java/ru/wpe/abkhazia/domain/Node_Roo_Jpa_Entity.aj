// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package ru.wpe.abkhazia.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import ru.wpe.abkhazia.domain.Node;

privileged aspect Node_Roo_Jpa_Entity {
    
    declare @type: Node: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Node.id;
    
    @Version
    @Column(name = "version")
    private Integer Node.version;
    
    public Long Node.getId() {
        return this.id;
    }
    
    public void Node.setId(Long id) {
        this.id = id;
    }
    
    public Integer Node.getVersion() {
        return this.version;
    }
    
    public void Node.setVersion(Integer version) {
        this.version = version;
    }
    
}
