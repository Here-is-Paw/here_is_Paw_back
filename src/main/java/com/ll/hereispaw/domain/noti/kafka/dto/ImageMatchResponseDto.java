package com.ll.hereispaw.domain.noti.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class ImageMatchResponseDto {
  private String status;

  @JsonProperty("saved_type")
  private String savedType;

  @JsonProperty("compared_type")
  private String comparedType;

  private String message;

  @JsonProperty("origin_member_id")
  private Long originMemberId;

  @JsonProperty("origin_post_id")
  private Long originPostId;

  private List<ImageMatchDto> matches;
}