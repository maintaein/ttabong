package com.ttabong.entity.user;

import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    //org_id가 auto_increment 기본키임
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private Integer orgId;

    // User와 1:1 관계
    @OneToOne
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime createdAt;
}
