package com.example.learning.hibernate6multitenancy.repository;

import com.example.learning.hibernate6multitenancy.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Persons extends JpaRepository<Person, Long> {
    static Person named(String name) {
        Person person = new Person();
        person.setName(name);
        return person;
    }
}