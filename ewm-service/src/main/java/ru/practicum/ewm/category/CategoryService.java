package ru.practicum.ewm.category;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.exception.CategoryNameNotUniqueException;
import ru.practicum.ewm.exception.CategoryNotFoundException;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto) throws CategoryNameNotUniqueException;

    CategoryDto update(CategoryDto categoryDto) throws CategoryNotFoundException, CategoryNameNotUniqueException;

    void delete(Long categoryId) throws CategoryNotFoundException;

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long categoryId) throws CategoryNotFoundException;
}
