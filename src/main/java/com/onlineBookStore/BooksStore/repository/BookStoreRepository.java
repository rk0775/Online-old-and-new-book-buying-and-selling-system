package com.onlineBookStore.BooksStore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onlineBookStore.BooksStore.Entities.BookStore;
import com.onlineBookStore.BooksStore.Entities.User;

public interface BookStoreRepository extends JpaRepository<BookStore, Integer> {
	public BookStore findByOwner(User user);

	public List<BookStore> findTop30ByOrderByStartdateDesc();

	@Query("SELECT COUNT(u) FROM BookStore u WHERE u.owner=:user")
	public int getCountOfStore(@Param("user") User user);

	public BookStore findByStoreId(int sid);

	public List<BookStore> findByCheckByAdmin(boolean flag);

	Page<BookStore> findByCheckByAdminTrue(Pageable pageable);

	Page<BookStore> findByCheckByAdminFalse(Pageable pageable);

}
