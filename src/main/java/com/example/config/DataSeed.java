package com.example.config;

import java.util.List;

import com.example.model.entities.Category;
import com.example.model.entities.Product;
import com.example.model.services.CategoryService;
import com.example.model.services.ProductService;

public class DataSeed {

    public static void seed() {
        CategoryService categoryService = new CategoryService();
        ProductService productService = new ProductService();

        if (categoryService.findAll().isEmpty()) {
            Category[] c = {
                    new Category(null, "Eletrônicos"),
                    new Category(null, "Livros"),
                    new Category(null, "Roupas"),
                    new Category(null, "Esportes"),
                    new Category(null, "Alimentos")
            };
            for (Category category : c) {
                categoryService.saveOrUpdate(category);
            }

        }

        if (productService.findAll().isEmpty()) {
            List<Category> categories = categoryService.findAll();
            Product[] p = {
                    new Product(null, "Smartphone", 10, 1999.90),
                    new Product(null, "Tênis Esportivo", 20, 299.99),
                    new Product(null, "Livro de Java", 15, 89.90),
                    new Product(null, "Notebook Gamer", 5, 5499.00),
                    new Product(null, "Fone Bluetooth", 30, 249.90),
                    new Product(null, "Smartwatch", 12, 899.00),
                    new Product(null, "Caixa de Som Portátil", 20, 349.90),
                    new Product(null, "Livro de Python", 18, 99.90),
                    new Product(null, "Romance Clássico", 25, 39.90),
                    new Product(null, "HQ Super-Herói", 10, 29.90),
                    new Product(null, "Livro Infantil Ilustrado", 15, 49.90),
                    new Product(null, "Camiseta Básica", 50, 39.90),
                    new Product(null, "Calça Jeans Masculina", 20, 129.90),
                    new Product(null, "Vestido Floral", 10, 159.90),
                    new Product(null, "Jaqueta Corta-Vento", 8, 199.90),
                    new Product(null, "Bola de Futebol", 25, 89.90),
                    new Product(null, "Raquete de Tênis", 10, 349.90),
                    new Product(null, "Camisa de Time Oficial", 15, 249.90),
                    new Product(null, "Kit de Yoga", 20, 179.90),
                    new Product(null, "Pacote de Café Gourmet", 40, 34.90)
            };

            // Associando categorias
            p[0].getCategories().add(categories.get(0)); // Smartphone - Eletrônicos
            p[1].getCategories().add(categories.get(3)); // Tênis Esportivo - Esportes
            p[2].getCategories().add(categories.get(1)); // Livro de Java - Livros
            p[3].getCategories().add(categories.get(0)); // Notebook Gamer
            p[4].getCategories().add(categories.get(0)); // Fone Bluetooth
            p[5].getCategories().add(categories.get(0)); // Smartwatch
            p[6].getCategories().add(categories.get(0)); // Caixa de Som
            p[7].getCategories().add(categories.get(1)); // Livro de Python
            p[8].getCategories().add(categories.get(1)); // Romance Clássico
            p[9].getCategories().add(categories.get(1)); // HQ
            p[10].getCategories().add(categories.get(1)); // Livro Infantil
            p[11].getCategories().add(categories.get(2)); // Camiseta
            p[12].getCategories().add(categories.get(2)); // Calça Jeans
            p[13].getCategories().add(categories.get(2)); // Vestido
            p[14].getCategories().add(categories.get(2)); // Jaqueta
            p[15].getCategories().add(categories.get(3)); // Bola de Futebol
            p[16].getCategories().add(categories.get(3)); // Raquete
            p[17].getCategories().add(categories.get(3)); // Camisa de Time
            p[18].getCategories().add(categories.get(3)); // Kit Yoga
            p[19].getCategories().add(categories.get(4)); // Café Gourmet

            // Salvando os produtos
            for (Product prod : p) {
                productService.saveOrUpdate(prod);
            }
        }

    }
}
