package com.ttabong.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class EmailCheckResponse {
    private boolean exists;
    private String message;
}
