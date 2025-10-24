package com.example.model.repositories;

import java.util.List;

import com.example.model.entities.Product;

import jakarta.persistence.EntityManager;

public class ProductRepository {

    private final EntityManager entityManager;

    public ProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Product obj) {
        entityManager.persist(obj);
    }

    public void update(Product obj) {
        entityManager.merge(obj);
    }

    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }

    public List<Product> findAll() {
        return entityManager.createQuery("SELECT c FROM Product c", Product.class).getResultList();
    }

    public void delete(Long id) {
        Product obj = findById(id);
        if (obj != null) {
            entityManager.remove(obj);
        }
    }
}