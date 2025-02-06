package com.ttabong.entity.user;

import com.ttabong.entity.sns.Review;
import com.ttabong.entity.recruit.Template;
import com.ttabong.entity.recruit.TemplateGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "business_reg_number", nullable = false, length = 30)
    private String businessRegNumber;

    @Column(name = "org_name", nullable = false, length = 100)
    private String orgName;

    @Column(name = "representative_name", nullable = false, length = 80)
    private String representativeName;

    @Column(name = "org_address", nullable = false, length = 200)
    private String orgAddress;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "org")
    private Set<Review> reviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "org")
    private Set<Template> templates = new LinkedHashSet<>();

    @OneToMany(mappedBy = "org")
    private Set<TemplateGroup> templateGroups = new LinkedHashSet<>();

}