package com.ttabong.entity.recruit;

import com.ttabong.entity.sns.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 new User() 막기
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Builder에서만 생성 가능
@Builder
@Table(name = "Recruit")
public class Recruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "deadline", nullable = false)
    private Instant deadline;

    @Column(name = "activity_date", nullable = false)
    private Date activityDate;

    @ColumnDefault("0.00")
    @Column(name = "activity_start", nullable = false, precision = 7, scale = 2)
    private BigDecimal activityStart;

    @ColumnDefault("0.00")
    @Column(name = "activity_end", nullable = false, precision = 7, scale = 2)
    private BigDecimal activityEnd;

    @ColumnDefault("0")
    @Column(name = "max_volunteer")
    private Integer maxVolunteer;

    @ColumnDefault("0")
    @Column(name = "participate_vol_count")
    private Integer participateVolCount;

    @Lob
    @Column(name = "status", nullable = false, columnDefinition = "enum('RECRUITING','RECRUITMENT_CLOSED','ACTIVITY_COMPLETED') DEFAULT 'PENDING'")
    private String status;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "recruit")
    private Set<Application> applications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "recruit")
    private Set<Review> reviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "recruit")
    private Set<VolunteerReaction> volunteerReactions = new LinkedHashSet<>();

    // 공고 삭제시 사용할 메서드
    public void delete() {
        this.isDeleted = true;
    }
}