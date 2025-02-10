package com.ttabong.entity.sns;

import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.user.Organization;
import com.ttabong.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 new Review() 막기
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Builder에서만 생성 가능
@Builder(toBuilder = true)
@Table(name = "Review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_review_id", foreignKey = @ForeignKey(name = "fk_parent_review_id"))
    private Review parentReview;

    @Column(name = "group_id", nullable = false)
    @ColumnDefault("1")
    private Integer groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id", foreignKey = @ForeignKey(name = "fk_review_recruit"))
    private Recruit recruit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_org"))
    private Organization org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_writer_id"))
    private User writer;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @ColumnDefault("false")
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ColumnDefault("true")
    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "img_count")
    private Integer imgCount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "parentReview", cascade = CascadeType.ALL)
    private Set<Review> childReviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private Set<ReviewComment> reviewComments = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }
}
