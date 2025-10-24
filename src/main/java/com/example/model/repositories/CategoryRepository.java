package com.example.model.repositories;

import java.util.List;

import com.example.model.entities.Category;

import jakarta.persistence.EntityManager;

public class CategoryRepository {

    private final EntityManager entityManager;

    public CategoryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Category obj) {
        entityManager.persist(obj);
    }

    public void update(Category obj) {
        entityManager.merge(obj);
    }

    public Category findById(Long id) {
        return entityManager.find(Category.class, id);
    }

    public List<Category> findAll() {
        return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    public void delete(Long id) {
        Category obj = findById(id);
        if (obj != null) {
            entityManager.remove(obj);
        }
    }
}