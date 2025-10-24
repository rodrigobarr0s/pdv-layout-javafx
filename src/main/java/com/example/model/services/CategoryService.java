package com.example.model.services;

import java.util.List;

import com.example.db.DB;
import com.example.model.entities.Category;
import com.example.model.repositories.CategoryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

public class CategoryService {

    public List<Category> findAll() {
        EntityManager entityManager = DB.getEntityManager();

        try {
            CategoryRepository repository = new CategoryRepository(entityManager);
            return repository.findAll();
        } catch (PersistenceException e) {
            throw new RuntimeException("Erro ao listar categorias: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public void saveOrUpdate(Category obj) {
        EntityManager entityManager = DB.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            CategoryRepository repository = new CategoryRepository(entityManager);

            if (obj.getId() == null) {
                repository.save(obj);
            } else {
                repository.update(obj);
            }

            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao salvar ou atualizar categoria: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public void remove(Category obj) {
        EntityManager entityManager = DB.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            CategoryRepository repository = new CategoryRepository(entityManager);
            repository.delete(obj.getId());
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException("Erro ao remover categoria: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }
}