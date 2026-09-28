package com.enterprise.hr.repository;

import com.enterprise.hr.domain.Employee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Employee lookup used by the leave workflow without exposing persistence to its controller. */
@Repository
public class LeaveEmployeeLookup {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Employee> findByEmail(String email) {
        try {
            return Optional.of(entityManager.createQuery(
                            "select e from Employee e where e.email = :email", Employee.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }
}
