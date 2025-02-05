package com.ttabong.entity.sns;

import com.ttabong.entity.recruit.Recruit;
import com.ttabong.entity.user.Organization;
import com.ttabong.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="Review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Review_id", nullable = false)
    private Integer ReviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_Review_id")
    private Review parentReview;

    @Column(name = "group_id")
    private Integer groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Recruit_id")
    private Recruit recruit;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Organization org;

    @OneToMany(mappedBy = "parentReview")
    private Set<Review> reviews = new LinkedHashSet<>();

    @OneToMany(mappedBy = "review")
    private Set<ReviewComment> reviewComments = new LinkedHashSet<>();

}