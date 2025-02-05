package com.ttabong.entity.sns;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Review_image")
public class ReviewImage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Integer id;

    @Column(name = "Review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "next_image_id")
    private Integer nextImageId;

    @OneToMany(mappedBy = "thumbnailImg")
    private Set<Review> reviews = new LinkedHashSet<>();

}