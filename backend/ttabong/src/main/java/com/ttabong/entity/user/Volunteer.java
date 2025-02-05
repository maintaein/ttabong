package com.ttabong.entity.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Volunteer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private Integer volunteerId;

    // User와 1:1 관계
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 봉사자 전용 정보들
    @Column(name = "preferred_time")
    private String preferredTime;

    @Column(name = "interest_theme")
    private String interestTheme;

    @Column(name = "duration_time")
    private String durationTime;

    private String region;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 1)
    private Character gender;

    @Column(name = "recommended_count")
    private Integer recommendedCount = 0;

    @Column(name = "not_recommended_count")
    private Integer notRecommendedCount = 0;
}
