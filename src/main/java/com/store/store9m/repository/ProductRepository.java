package com.store.store9m.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.store9m.model.Product;

public interface ProductRepository extends JpaRepository<Product,Integer>{
	
	List<Product> findByIsActiveTrue();

	List<Product> findByCategory(String category);
	
	List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);
	
}