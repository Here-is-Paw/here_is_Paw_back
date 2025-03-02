package com.ll.hereispaw.domain.payment.point.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointDto {
    @JsonProperty("username")
    private String username;

    @JsonProperty("points")
    private Integer points;
}
