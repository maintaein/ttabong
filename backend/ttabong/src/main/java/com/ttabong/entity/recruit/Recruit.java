package com.ttabong.entity.recruit;

import com.ttabong.entity.sns.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "Recruit")
@Getter
@Setter
public class Recruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Recruit_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "deadline", nullable = false)
    private Instant deadline;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(name = "activity_time", nullable = false, length = 50)
    private String activityTime;

    @ColumnDefault("0")
    @Column(name = "max_volunteer")
    private Integer maxVolunteer;

    @ColumnDefault("0")
    @Column(name = "participate_vol_count")
    private Integer participateVolCount;

    @Lob
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('ALL', 'ACTIVE', 'INACTIVE') DEFAULT 'ALL'")
    private String status;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "recruit")
    private Set<Application> applications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "recruit")
    private Set<Review> reviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "recruit")
    private Set<VolunteerReaction> volunteerReactions = new LinkedHashSet<>();

}