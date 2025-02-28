package com.ll.hereispaw.domain.noti.entity;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.noti.person.entity.Person;
import com.ll.hereispaw.global.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Noti extends BaseEntity {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sender_id")
  private Person sender;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "receiver_id")
  private Person receiver;

  private String eventName;

  private String message;

  private Long postId;

  private boolean read;

  public void markAsRead() {
    this.read = true;
  }


}
