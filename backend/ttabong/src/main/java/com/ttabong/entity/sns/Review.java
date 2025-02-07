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
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 new User() 막기
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Builder에서만 생성 가능
@Builder(toBuilder = true)
@Table(name = "Review")
public class Review {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_Review_id")
    private Review parentReview;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Review_id", nullable = false, columnDefinition = "INT")
    private Long id;

    @Column(name = "group_id", columnDefinition = "INT")
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Recruit_id")
    private Recruit recruit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnDefault("1")
    @Column(name = "is_public")
    private Boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_img")
    private ReviewImage thumbnailImg;

    @Column(name = "img_count")
    private Integer imgCount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "parentReview")
    private Set<Review> reviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "review")
    private Set<ReviewComment> reviewComments = new LinkedHashSet<>();


}