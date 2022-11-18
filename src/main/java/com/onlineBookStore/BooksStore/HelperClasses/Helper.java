package com.onlineBookStore.BooksStore.HelperClasses;

import java.util.ArrayList;
import java.util.List;

import com.onlineBookStore.BooksStore.Entities.Book;
import com.onlineBookStore.BooksStore.Entities.BookRating;
import com.onlineBookStore.BooksStore.repository.BookRatingRepository;

public class Helper {
	BookRatingRepository bookRatingRepository;

	public Helper() {
	}

	public Helper(BookRatingRepository bookRatingRepository) {
		this.bookRatingRepository = bookRatingRepository;
	}

	static public List<Book> removeFormBookList(List<Book> list, int id) {
		List<Book> books = new ArrayList<>();
		for (Book b : list) {
			if (b.getBookId() != id)
				books.add(b);
		}
		return books;
	}

	public int getRating(Book book) {
		int totalRate = 0, totalPerson = 0;
		List<BookRating> bookRating = bookRatingRepository.findByBook(book);
		System.out.println(bookRating.size());
		for (BookRating br : bookRating) {
			totalRate += br.getBookRate();
			totalPerson++;
		}
		if (bookRating.isEmpty() || totalRate == 0 || totalPerson == 0)
			return 0;
		else
			return totalRate / totalPerson;
	}

}
