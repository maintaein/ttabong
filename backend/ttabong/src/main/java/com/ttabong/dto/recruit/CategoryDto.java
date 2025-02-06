package com.ttabong.dto.recruit;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long categoryId;
    private String name;
    private Long parentId;
}
