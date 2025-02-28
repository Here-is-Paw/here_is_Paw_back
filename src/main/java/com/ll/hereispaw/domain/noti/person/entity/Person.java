package com.ll.hereispaw.domain.noti.person.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ll.hereispaw.domain.member.mypet.entity.MyPet;
import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import com.ll.hereispaw.global.jpa.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Slf4j
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "MEMBER")
public class Person extends BaseEntity {
    @Column(unique = true, length = 30)
    private String username;

    private String password;

    @Column(length = 30)
    private String nickname;

    @Column(unique = true, length = 50)
    private String apiKey;

}