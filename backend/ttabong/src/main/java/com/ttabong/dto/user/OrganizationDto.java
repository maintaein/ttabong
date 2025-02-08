package com.ttabong.dto.user;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationDto {
    private Long orgId;
    private Long userId;
    private String businessRegNumber;
    private String orgName;
    private String representativeName;
    private String orgAddress;
    private LocalDateTime createdAt;
}
