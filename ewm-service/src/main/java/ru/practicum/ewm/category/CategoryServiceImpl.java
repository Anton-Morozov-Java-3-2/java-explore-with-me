package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.exception.CategoryNameNotUniqueException;
import ru.practicum.ewm.exception.CategoryNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) throws CategoryNameNotUniqueException {
        try {
            Category category = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));
            log.info("Create {}", category);
            return categoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameNotUniqueException(e.getMessage());
        }
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) throws CategoryNotFoundException, CategoryNameNotUniqueException {
        checkExistCategory(categoryDto.getId());
        try {
            Category category = categoryRepository.save(categoryMapper.toCategory(categoryDto));
            log.info("Update {}", category);
            return categoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameNotUniqueException(e.getMessage());
        }
    }

    @Override
    public void delete(Long categoryId) throws CategoryNotFoundException {
        checkExistCategory(categoryId);
        categoryRepository.deleteById((long) categoryId);
        log.info("Delete category id= {}", categoryId);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        List<CategoryDto> list = categoryRepository.findAll(pageRequest).stream()
                                                    .map(categoryMapper::toCategoryDto)
                                                    .collect(Collectors.toList());
        log.info("Get categories page= {}, size= {}", from, size);
        return list;
    }

    @Override
    public CategoryDto getById(Long categoryId) throws CategoryNotFoundException {
        Optional<Category> optionalCategory = categoryRepository.findById((long) categoryId);
        if (optionalCategory.isPresent()) {
            log.info("Get {}", optionalCategory.get());
            return categoryMapper.toCategoryDto(optionalCategory.get());
        } else {
            throw new CategoryNotFoundException(CategoryNotFoundException.createMessage(categoryId));
        }
    }

    private void checkExistCategory(Long categoryId) throws CategoryNotFoundException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(CategoryNotFoundException.createMessage(categoryId));
        }
    }
}
