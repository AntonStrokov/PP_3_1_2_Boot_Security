package ru.kata.spring.boot_security.demo.dao.impl;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void addUser(User user) {
		entityManager.persist(user);
	}

	@Override
	public void updateUser(User user) {
		entityManager.merge(user);
	}

	@Override
	public void removeUser(Long id) {
		entityManager.createQuery("DELETE FROM User u WHERE u.id = :id")
				.setParameter("id", id)
				.executeUpdate();
	}

	@Override
	public Optional<User> getUserById(Long id) {
		return Optional.ofNullable(entityManager.find(User.class, id));
	}

	@Override
	public List<User> getAllUsers() {

		return entityManager.createQuery(
						"SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles", User.class)
				.getResultList();
	}


	@Override
	public Optional<User> findByEmail(String email) {

		return entityManager.createQuery(
						"SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email", User.class)
				.setParameter("email", email)
				.getResultStream()
				.findFirst();
	}

}
