package br.com.zup.bootcamp.proposta.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionWrapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public <T> T create(T object) {
        entityManager.persist(object);
        return object;
    }

    @Transactional
    public <T> T update(T object) {
        entityManager.merge(object);
        return object;
    }

}
