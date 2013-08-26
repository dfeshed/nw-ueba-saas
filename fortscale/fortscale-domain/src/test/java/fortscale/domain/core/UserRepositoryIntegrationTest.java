package fortscale.domain.core;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import fortscale.domain.AbstractIntegrationTest;
import fortscale.domain.core.dao.UserRepository;

public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	UserRepository repository;

	@Test
	public void savesCustomerCorrectly() {

		EmailAddress email = new EmailAddress("alicia@keys.com");

		User dave = new User("AliciaKeys");
		dave.setEmailAddress(email);
//		dave.add(new Address("27 Broadway", "New York", "United States"));

		User result = repository.save(dave);
		assertThat(result.getId(), is(notNullValue()));
	}

	@Test
	public void readsUserByEmail() {

		EmailAddress email = new EmailAddress("alicia@keys.com");
		User alicia = new User("AliciaKeys");
		alicia.setEmailAddress(email);

		repository.save(alicia);

		User result = repository.findByEmailAddress(email);
		assertThat(result, is(alicia));
	}

	@Test(expected = DuplicateKeyException.class)
	public void preventsDuplicateEmail() {

		User dave = repository.findByEmailAddress(new EmailAddress("dave@dmband.com"));

		User anotherDave = new User("dkoler");
		anotherDave.setEmailAddress(dave.getEmailAddress());

		repository.save(anotherDave);
	}
	
	@Test
	public void resavingDave() {
		User dave = repository.findByEmailAddress(new EmailAddress("dave@dmband.com"));
		repository.save(dave);
	}
	@Test
	public void updatingDave() {
		User dave = repository.findByEmailAddress(new EmailAddress("dave@dmband.com"));
		dave.setFirstname("davi");
		repository.save(dave);
	}
}
