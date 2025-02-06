package com.ttabong.dto.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateGroupDto {

    private Long groupId;
    private Long orgId;
    private String groupName;
    private Boolean isDeleted;

}
