package com.onlineBookStore.BooksStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onlineBookStore.BooksStore.Entities.BookCategorys;

public interface BookCategoryRepository extends JpaRepository<BookCategorys, Integer> {
	public List<BookCategorys> findByCategoryName(String category);

	@Query(value = "Delete from BookCategorys  where categoryId=:id", nativeQuery = true)
	public Boolean deleteByCategoryId(@Param("id") int cid);

}
