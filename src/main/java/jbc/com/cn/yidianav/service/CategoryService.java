package jbc.com.cn.yidianav.service;

import jbc.com.cn.yidianav.dto.CategoryDTO;
import jbc.com.cn.yidianav.entity.Category;
import jbc.com.cn.yidianav.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryDTO> getTreeStructure() {
        List<Category> rootCategories = categoryRepository.findByParentIdIsNullOrderBySortOrder();
        return rootCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIcon(category.getIcon());
        dto.setParentId(category.getParentId());
        dto.setSortOrder(category.getSortOrder());

        List<Category> children = categoryRepository.findByParentIdOrderBySortOrder(category.getId());
        if (!children.isEmpty()) {
            dto.setChildren(children.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setName(categoryDetails.getName());
            category.setIcon(categoryDetails.getIcon());
            category.setParentId(categoryDetails.getParentId());
            category.setSortOrder(categoryDetails.getSortOrder());
            return categoryRepository.save(category);
        }
        return null;
    }

    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
