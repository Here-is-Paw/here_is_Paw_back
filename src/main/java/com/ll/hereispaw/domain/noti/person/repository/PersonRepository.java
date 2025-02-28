package com.ll.hereispaw.domain.noti.person.repository;

import com.ll.hereispaw.domain.noti.person.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
