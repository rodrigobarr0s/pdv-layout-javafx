package com.example.model.services;

import java.util.List;

import com.example.db.DB;
import com.example.model.entities.Product;
import com.example.model.repositories.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

public class ProductService {

    public List<Product> findAll() {
        EntityManager entityManager = DB.getEntityManager();

        try {
            ProductRepository repository = new ProductRepository(entityManager);
            return repository.findAll();
        } catch (PersistenceException e) {
            throw new RuntimeException("Erro ao listar categorias: " + e.getMessage());
        } finally {
            entityManager.close();
        }
    }

    public void saveOrUpdate(Product obj) {
        EntityManager entityManager = DB.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            ProductRepository repository = new ProductRepository(entityManager);

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

    public void remove(Product obj) {
        EntityManager entityManager = DB.getEntityManager();

        try {
            entityManager.getTransaction().begin();
            ProductRepository repository = new ProductRepository(entityManager);
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