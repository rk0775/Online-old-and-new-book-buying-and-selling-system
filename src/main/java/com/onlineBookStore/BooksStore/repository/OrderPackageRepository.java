package com.onlineBookStore.BooksStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onlineBookStore.BooksStore.Entities.BookOrder;
import com.onlineBookStore.BooksStore.Entities.OrderPackage;

public interface OrderPackageRepository extends JpaRepository<OrderPackage, Integer> {
	public List<OrderPackage> findByOrder(BookOrder order);

	public OrderPackage findByBookIsbnNumber(String str);

	@Query("SELECT COUNT(u) FROM OrderPackage u WHERE u.bookIsbnNumber=:num")
	public int getCountOfIsbn(@Param("num") String num);

	public OrderPackage findByOrderPackageId(int id);
}
