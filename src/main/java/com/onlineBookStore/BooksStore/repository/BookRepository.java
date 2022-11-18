package com.onlineBookStore.BooksStore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.onlineBookStore.BooksStore.Entities.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	public Book findByBookId(int id);

	public List<Book> findByBookTitle(String bookName);

	// @Query("select b from book b where b.seller=uid")
	@Query("from Book as b where b.seller.userId=:uid")
	Page<Book> findAllPageByUserId(int uid, Pageable pageable);

	@Query("select e from Book e where e.bookTitle like %:name%")
	List<Book> getLikeBookName(@Param("name") String name); // fetch list of employee containing name

	@Query("select b from Book b where b.bookAuthor like %:name%")
	List<Book> getLikeAutherName(@Param("name") String name); // fetch list of employee containing name

	@Query("select b from Book b where b.bookAuthor like %:aname% and b.bookTitle like %:bname%")
	List<Book> getLikeAutherNameAndBookName(@Param("aname") String aname, @Param("bname") String bname);

	@Query("SELECT COUNT(u) FROM Book u WHERE u.bookType=:type")
	public int getCountByTypeBook(@Param("type") String type);

	// book refered by user ->is 0
	Page<Book> findByBookQuantityGreaterThanAndBookTitleContainingAndSaleTrue(int bookQuntity, String name,
			Pageable pageable);

	// book refered by user ->is 0
	Page<Book> findByStoreIdIsAndBookQuantityGreaterThanAndSaleTrue(int storePresent, int bookQuntity,
			Pageable pageable);

	// book refered by user ->is 0 searchng book by name like
	Page<Book> findByStoreIdIsAndBookQuantityGreaterThanAndBookTitleContainingAndSaleTrue(int storePresent,
			int bookQuntity, String bookName, Pageable pageable);

	// book refered by store ->not 0
	Page<Book> findByStoreIdIsNotAndBookQuantityGreaterThanAndSaleTrue(int storePresent, int bookQuntity,
			Pageable pageable);

	// book refered by store ->not 0
	Page<Book> findByStoreIdIsNotAndBookQuantityGreaterThanAndIsBestOfYearTrueAndSaleTrue(int storePresent,
			int bookQuntity, Pageable pageable);

	// book refered by store ->not 0
	Page<Book> findByStoreIdIsNotAndBookQuantityGreaterThanAndIsBestOfYearFalseAndSaleTrue(int storePresent,
			int bookQuntity, Pageable pageable);

	// book refered by store ( ->not 0 ) language
	Page<Book> findByStoreIdIsNotAndBookQuantityGreaterThanAndBookLanguageAndSaleTrue(int storePresent, int bookQuntity,
			String language, Pageable pageable);

	// book refered by store ( ->not 0 ) searching book name like
	Page<Book> findByStoreIdIsNotAndBookQuantityGreaterThanAndBookTitleContainingAndSaleTrue(int storePresent,
			int bookQuntity, String bookName, Pageable pageable);

	// Page<Book> findByBookTitleContaining(String bname, Pageable pageable);

}
