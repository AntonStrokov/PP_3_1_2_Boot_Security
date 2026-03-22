package ru.kata.spring.boot_security.demo.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleDaoImpl implements RoleDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void addRole(Role role) {
		entityManager.persist(role);
	}

	@Override
	public Optional<Role> getRoleByName(String name) {
		return entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
				.setParameter("name", name)
				.setMaxResults(1)
				.getResultList()
				.stream()
				.findFirst();
	}

	@Override
	public List<Role> getAllRoles() {
		return entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();
	}

	@Override
	public Optional<Role> getRoleById(Long id) {
		return Optional.ofNullable(entityManager.find(Role.class, id));
	}

	@Override
	public List<Role> getRolesByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) return Collections.emptyList();
		return entityManager.createQuery("SELECT r FROM Role r WHERE r.id IN :ids", Role.class)
				.setParameter("ids", ids)
				.getResultList();
	}
}