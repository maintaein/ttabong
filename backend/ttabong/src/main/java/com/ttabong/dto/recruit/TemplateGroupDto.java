package com.ttabong.dto.recruit;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateGroupDto {
    private Long groupId;
    private Long orgId;
    private String groupName;
    private Boolean isDeleted;
}
