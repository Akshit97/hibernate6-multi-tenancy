package com.example.learning.hibernate6multitenancy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

@Entity
@Getter
@Setter
public class Person {

    @TenantId
    private String tenant;

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // getter and setter skipped for brevity.
}