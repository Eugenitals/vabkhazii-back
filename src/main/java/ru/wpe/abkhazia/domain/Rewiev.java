package ru.wpe.abkhazia.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Rewiev {

    @NotNull
    private int rating;

    @NotNull
    private String title;

    @NotNull
    private String comment;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date postingDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Entity entity;

    @JsonIgnore
    public Entity getEntity() {
        return this.entity;
    }

    public static List<Rewiev> findAllRewievs() {
        return entityManager().createQuery("SELECT o FROM Rewiev o ORDER BY o.postingDate ASC", Rewiev.class).getResultList();
    }

    public Rewiev() {
    }

    public Rewiev(Builder builder) {
        rating = builder.rating;
        title = builder.title;
        comment = builder.comment;
        postingDate = builder.postingDate;
        entity = builder.entity;
    }

    public static class Builder {
        private int rating;
        private String title;
        private String comment;
        private Date postingDate;
        private Entity entity;

        public Rewiev build() {
            return new Rewiev(this);
        }

        public Builder rating(int _rating) {
            rating = _rating;
            return this;
        }

        public Builder title(String _title) {
            title = _title;
            return this;
        }

        public Builder comment(String _comment) {
            comment = _comment;
            return this;
        }

        public Builder postingDate(Date _postingDate) {
            postingDate = _postingDate;
            return this;
        }

        public Builder entity(Entity _entity) {
            entity = _entity;
            return this;
        }
    }
}
