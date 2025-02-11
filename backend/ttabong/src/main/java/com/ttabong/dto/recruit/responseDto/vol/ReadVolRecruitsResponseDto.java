package com.ttabong.dto.recruit.responseDto.vol;

import com.ttabong.entity.recruit.Template;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadVolRecruitsResponseDto {
    private Integer templateId;
    private Integer categoryId;
    private String title;
    private String activityLocation;
    private String status;
    private String imageId;
    private String contactName;
    private String contactPhone;
    private String description;
    private Instant createdAt;

    public static ReadVolRecruitsResponseDto from(Template template) {
        return ReadVolRecruitsResponseDto.builder()
                .templateId(template.getId())
                .categoryId(template.getCategory() != null ? template.getCategory().getId() : null)
                .title(template.getTitle())
                .activityLocation(template.getActivityLocation())
                .status(template.getStatus())
                .imageId(template.getThumbnailImage() != null ? template.getThumbnailImage().getImageUrl() : null)
                .contactName(template.getContactName())
                .contactPhone(template.getContactPhone())
                .description(template.getDescription())
                .createdAt(template.getCreatedAt())
                .build();
    }
}
