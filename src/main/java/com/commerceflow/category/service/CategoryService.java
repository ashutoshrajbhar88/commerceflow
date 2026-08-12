package com.commerceflow.category.service;

import com.commerceflow.category.Category;
import com.commerceflow.category.dto.CategoryRequest;
import com.commerceflow.category.dto.CategoryResponse;
import com.commerceflow.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import com.commerceflow.exception.DuplicateResourceException;
import java.util.List;

import com.commerceflow.exception.ResourceNotFoundException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse createCategory(CategoryRequest request){

        if(categoryRepository.existsByName(request.getName())){
            throw new DuplicateResourceException("Category already exists with name: " + request.getName());
        }

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse();

        response.setId(savedCategory.getId());
        response.setName(savedCategory.getName());
        response.setDescription(savedCategory.getDescription());
        response.setCreatedAt(savedCategory.getCreatedAt());
        response.setUpdatedAt(savedCategory.getUpdatedAt());

        return response;

    }

    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(category -> {

                    CategoryResponse response = new CategoryResponse();

                    response.setId(category.getId());
                    response.setName(category.getName());
                    response.setDescription(category.getDescription());
                    response.setCreatedAt(category.getCreatedAt());
                    response.setUpdatedAt(category.getUpdatedAt());

                    return response;
                })
                .toList();
    }
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id
                ));

        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        return response;
    }

    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request
    ) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id
                ));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        CategoryResponse response = new CategoryResponse();

        response.setId(updatedCategory.getId());
        response.setName(updatedCategory.getName());
        response.setDescription(updatedCategory.getDescription());
        response.setCreatedAt(updatedCategory.getCreatedAt());
        response.setUpdatedAt(updatedCategory.getUpdatedAt());

        return response;
    }

    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + id
                ));

        categoryRepository.delete(category);
    }


}
