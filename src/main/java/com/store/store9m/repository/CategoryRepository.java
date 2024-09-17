package com.store.store9m.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.store9m.model.Category;

public interface CategoryRepository extends JpaRepository <Category,Integer>{

	public Boolean existsByName(String name);
	
	public List<Category> findByIsActiveTrue();
	
}
