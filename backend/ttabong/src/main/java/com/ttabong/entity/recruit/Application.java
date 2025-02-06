package com.ttabong.entity.recruit;

import com.ttabong.entity.user.Volunteer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Recruit_id", nullable = false)
    private Recruit recruit;

    @Lob
    @Column(name = "status", nullable = false, columnDefinition = "enum('PENDING','APPROVED','REJECTED','COMPLETED','AUTO_CANCEL','NO_SHOW') DEFAULT 'PENDING'")
    private String status;

    @ColumnDefault("0")
    @Column(name = "evaluation_done")
    private Boolean evaluationDone;

    @ColumnDefault("0")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}