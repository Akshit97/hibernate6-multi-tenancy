package com.example.learning.hibernate6multitenancy;

import com.example.learning.hibernate6multitenancy.model.Person;
import com.example.learning.hibernate6multitenancy.repository.Persons;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Slf4j
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
class ApplicationTests {

	static final String PIVOTAL = "PIVOTAL";
	static final String VMWARE = "VMWARE";

	@Autowired
	Persons persons;

	@Autowired
	TransactionTemplate txTemplate;

	@Autowired
	TenantIdentifierResolver currentTenant;

	@Test
	void saveAndLoadPerson() {

		Person adam = createPerson(PIVOTAL, "Adam");
		Person eve = createPerson(VMWARE, "Eve");

		assertThat(adam.getTenant()).isEqualTo(PIVOTAL);
		assertThat(eve.getTenant()).isEqualTo(VMWARE);

		currentTenant.setCurrentTenant(VMWARE);
		assertThat(persons.findAll()).extracting(Person::getName).containsExactly("Eve");

		currentTenant.setCurrentTenant(PIVOTAL);
		assertThat(persons.findAll()).extracting(Person::getName).containsExactly("Adam");
	}

	@Test
	void findById() {

		Person adam = createPerson(PIVOTAL, "Adam");
		Person vAdam = createPerson(VMWARE, "Adam");

		currentTenant.setCurrentTenant(VMWARE);
		assertThat(persons.findById(vAdam.getId()).get().getTenant()).isEqualTo(VMWARE);
		assertThat(persons.findById(adam.getId())).isEmpty();
	}

	@Test
	void updatePerson() {
		Person eve = createPerson(VMWARE, "Eve");
		log.info("createPerson");

		currentTenant.setCurrentTenant(VMWARE);

		eve.setName("New Name");
		persons.save(eve);
		log.info("Update Person");


		List<Person> personList = persons.findAll();
		log.info(String.valueOf(personList));
	}

	private Person createPerson(String schema, String name) {

		currentTenant.setCurrentTenant(schema);

		Person adam = txTemplate.execute(tx ->
				{
					Person person = Persons.named(name);
					return persons.save(person);
				}
		);

		assertThat(adam.getId()).isNotNull();
		return adam;
	}
}