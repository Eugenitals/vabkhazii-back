package ru.wpe.abkhazia.domain;

import java.util.ArrayList;
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

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Node {

    @NotNull
    private Boolean active = true;

    @Transient
    protected Boolean leaf = false;

    @NotNull
    private String name;

    @Size(max = 1000)
    private String description;

    private String icon;

    @ManyToOne(optional = true, cascade = CascadeType.DETACH)
    private ru.wpe.abkhazia.domain.Node parent;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private Set<ru.wpe.abkhazia.domain.Node> entities = new HashSet<ru.wpe.abkhazia.domain.Node>();

    @JsonIgnore
    public ru.wpe.abkhazia.domain.Node getParent() {
        return this.parent;
    }

    @JsonIgnore
    public Set<Node> getEntities() {
        return this.entities;
    }


    private static Set<Node> collectParents(List<Node> nodeList, Node end) {
        Set<Node> result = new HashSet<Node>();
        for (Node node: nodeList) {
            if (node.getParent() == null || node.getParent().equals(end)) {
                continue;
            }
            result.add(node.getParent());
        }
        return result;
    }

    public static List<Node> findNodesByParent(Node parent, long regionId) {
        if (parent == null) {
            throw new IllegalArgumentException("The parent argument is required");
        }
        List<Node> resultList = new ArrayList<Node>();
        Set<Node> availableList = new HashSet<Node>();
        Set<Node> currentList;

        EntityManager em = Node.entityManager();

        TypedQuery<Node> q = em.createQuery("SELECT o FROM Node AS o WHERE o.region = :region AND o.active = :active", Node.class);
        q.setParameter("region", Region.findRegion(regionId));
        q.setParameter("active", true);

        availableList.addAll(q.getResultList());
        currentList = collectParents(new ArrayList<Node>(availableList), parent);
        availableList.addAll(currentList);

        q = em.createQuery("SELECT o FROM Node AS o WHERE o IN :childs AND o.active = :active", Node.class);
        while (currentList.size() > 0) {

            q.setParameter("childs", currentList);
            q.setParameter("active", true);
            currentList = collectParents(q.getResultList(), parent);
            availableList.addAll(currentList);
        }

        q = em.createQuery("SELECT o FROM Node AS o WHERE o IN :avaliable AND o.parent = :parent ORDER BY o.rate DESC, o.name ASC", Node.class);
        q.setParameter("parent", parent);
        q.setParameter("avaliable", availableList);


        for (Node node : q.getResultList()) {
            if (node instanceof Entity) {
                Entity entity = (Entity) node;
                Set<Resource> resList = new HashSet<Resource>();

                for (Resource res : entity.getResources()) {
                    resList.add(Resource.findResource(res.getId()));
                }
                entity.setResources(resList);
            }

            resultList.add(node);
        }

        return resultList;
    }

    public static TypedQuery<Node> findNodesByActiveNot(Boolean active) {
        if (active == null) throw new IllegalArgumentException("The active argument is required");
        EntityManager em = Node.entityManager();
        TypedQuery<Node> q = em.createQuery("SELECT o FROM Node AS o WHERE o.active IS NOT :active", Node.class);
        q.setParameter("active", active);
        return q;
    }

    public static List<Node> findRootNodes(long regionId) {
        EntityManager em = Node.entityManager();
        TypedQuery<Node> q = em.createQuery("SELECT o FROM Node AS o WHERE o.parent IS NULL", Node.class);
        Node root = q.getSingleResult();

        return findNodesByParent(root, regionId);
    }

    @Override
    public String toString() {
        return "Категория {" + this.getName() + " (" + this.getId() + ")}";
    }

    public static List<String> categoryList() {
        int size = 210;
        List<String> categoryList = new ArrayList<String>();
        for (int i = 1; i <= size; i++) {
            categoryList.add("icon_" + String.format("%03d", i) + ".PNG");
        }
        return categoryList;
    }

}
