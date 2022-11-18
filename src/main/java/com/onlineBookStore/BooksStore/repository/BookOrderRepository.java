package com.onlineBookStore.BooksStore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.onlineBookStore.BooksStore.Entities.Book;
import com.onlineBookStore.BooksStore.Entities.BookOrder;

public interface BookOrderRepository extends JpaRepository<BookOrder, Integer> {
	/*
	 * @Query("from BookOrder as b where b.orderedUser.userId=:uId") public
	 * List<BookOrder> findByOrderedUser(@Param("uId") int userId);
	 */

	public List<BookOrder> findByOrderedBooks(Book book);

	public List<BookOrder> findTop30ByOrderByOrderDateDesc();

	public BookOrder findByOrderId(int id);

//	@Query("select f from BookOrder f where f.orderStatus=1")
	Page<BookOrder> findByOrderStatusIs(String name, Pageable pageable);
	// public BookOrder findByOrderedBooks(Book book);

	// @Query("select f from BookOrder f where f.orderStatus=1 or f.orderStatus=2")
	Page<BookOrder> findByOrderStatusIsOrOrderStatusIs(String name, String name2, Pageable pageable);

	// public BookOrder findByOrderedBooks(Book book);
	@Query("select f from BookOrder f where f.orderStatus=1 or f.orderStatus=2")
	List<BookOrder> findByOrderStatus(String name, String name2);
	// public BookOrder findByOrderedBooks(Book book);

}
