package com.salesphere.salesphere.repositories;

import com.salesphere.salesphere.models.Category;
import com.salesphere.salesphere.models.enums.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryEnum(CategoryEnum categoryEnum);
}
