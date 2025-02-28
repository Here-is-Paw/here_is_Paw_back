package com.ll.hereispaw.domain.noti.person.service;

import com.ll.hereispaw.domain.noti.person.entity.Person;
import com.ll.hereispaw.domain.noti.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;

  public Person findById(Long id) {
    return personRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Person not found"));
  }

}
