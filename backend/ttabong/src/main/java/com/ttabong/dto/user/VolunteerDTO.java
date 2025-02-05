package com.ttabong.dto.user;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerDTO {
    private Long volunteerId;
    private Long userId;
    private String preferredTime;
    private String interestTheme;
    private String durationTime;
    private String region;
    private LocalDate birthDate;
    private String gender;
    private Integer recommendedCount;
    private Integer notRecommendedCount;
}
