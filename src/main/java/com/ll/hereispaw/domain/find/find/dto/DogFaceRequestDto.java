package com.ll.hereispaw.domain.find.find.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogFaceRequestDto {
  private String type;
  private String image;
  private String postType;
  private Long postId;
  private Long postMemberId;
}