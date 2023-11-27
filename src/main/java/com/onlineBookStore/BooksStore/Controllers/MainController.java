package com.onlineBookStore.BooksStore.Controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
//import javax.validation.Valid;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswodEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.onlineBookStore.BooksStore.Entities.Book;
import com.onlineBookStore.BooksStore.Entities.BookCategorys;
import com.onlineBookStore.BooksStore.Entities.BookRating;
import com.onlineBookStore.BooksStore.Entities.User;
import com.onlineBookStore.BooksStore.Entities.UserFeedback;
import com.onlineBookStore.BooksStore.HelperClasses.EmailActions;
import com.onlineBookStore.BooksStore.HelperClasses.Helper;
import com.onlineBookStore.BooksStore.HelperClasses.StringOperations;
import com.onlineBookStore.BooksStore.repository.BookCategoryRepository;
import com.onlineBookStore.BooksStore.repository.BookRatingRepository;
import com.onlineBookStore.BooksStore.repository.BookRepository;
import com.onlineBookStore.BooksStore.repository.BookStoreRepository;
import com.onlineBookStore.BooksStore.repository.FeedbackRepository;
import com.onlineBookStore.BooksStore.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

@Controller
public class MainController {
	// autowired the user dao class
	@Autowired
	UserRepository userRepository;
	@Autowired
	BookRepository bookRepository;
	@Autowired
	BookCategoryRepository bookCategoryRepositor;
	@Autowired
	FeedbackRepository feedbackRepository;
	@Autowired
	BCryptPasswordEncoder encPassword;
	@Autowired
	private JavaMailSender emailSender;
	/*
	 * @Autowired BookRatingRepository bookRatingRepository;
	 */

	@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request, Principal p, Model model) {
		if (request.isUserInRole("ROLE_ADMIN")) {
			User user = userRepository.findByUserEmail(p.getName());
			System.out.println("User name : " + user.getUserName());
			if (user.isEnabled()) {

				return "redirect:/admin/adminDashboard";
			} else {
				model.addAttribute("tagName", "login");
				model.addAttribute("navbar", "bg-fyellow");
				model.addAttribute("message", "just");
				model.addAttribute("alert", "notVerify");
				System.out.println("not verify");
				return "/loginRegister";
			}
		} else {
			User user = userRepository.findByUserEmail(p.getName());
			System.out.println("User name : " + user.getUserName());
			if (user.isEnabled()) {
				return "redirect:/user/browseBooks/all,0";
			} else {
				model.addAttribute("tagName", "login");
				model.addAttribute("navbar", "bg-fyellow");
				model.addAttribute("message", "just");
				model.addAttribute("alert", "notVerify");
				System.out.println("not verify");
				return "/loginRegister";
			}
		}
	}

	@RequestMapping("/")
	public String indexPage(Model model) {
		model.addAttribute("navbar", "none");

		List<Book> books = new ArrayList<>();
		// only store books present
		for (Book book : bookRepository.findAll()) {
			if (bookStoreRepository.getCountOfStore(book.getSeller()) > 0
					&& book.getSeller().getBookStore().isCheckByAdmin() && book.isSale())
				books.add(book);
		}
		model.addAttribute("books", books);

		return "header";
	}

	@ModelAttribute
	public void getData(Model model, Principal p) {
		Helper helper = new Helper(bookRatingRepository);
		model.addAttribute("h", helper);
		if (p != null) {
			System.out.println(" Principle P :  " + p.getName());
			User user = userRepository.findByUserEmail(p.getName());
			if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
				model.addAttribute("path", "/admin/adminDashboard");
			} else
				model.addAttribute("path", "/user/browseBooks/all,0");
			model.addAttribute("loginUser", user);
			model.addAttribute("fname", StringOperations.getOnlyUserFirstName(user.getUserName()));
		} else {
			model.addAttribute("loginUser", "none");
		}

		model.addAttribute("user", new User());
	}

	@RequestMapping("/loginRegister")
	public String loginRegister(Model model) {
		model.addAttribute("tagName", "login");
		model.addAttribute("navbar", "bg-fyellow");
		model.addAttribute("message", "just");
		model.addAttribute("alert", "none");

		return "loginRegister";
	}

	// for show the specific book info
	@RequestMapping("/bookInfo/{bookId}")
	public String singleBookinformation(@PathVariable("bookId") Integer bookId, Model model) {
		Book book = bookRepository.findByBookId(bookId);
		HashSet<Book> bookList = new HashSet();

		// System.out.println("category list " + book.getCategory().toString());
		for (int i = 0; i < book.getCategory().size(); i++) {
			List<BookCategorys> bCat = bookCategoryRepository
					.findByCategoryName(book.getCategory().get(i).getCategoryName());

			for (BookCategorys c : bCat) {
				System.out.println("Inner for books : " + c.getBookCategory().getBookTitle());
				if (bookStoreRepository.getCountOfStore(c.getBookCategory().getSeller()) > 0
						&& book.getSeller().getBookStore().isCheckByAdmin() && book.isSale())// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
				{
					bookList.add(c.getBookCategory());
					// System.out.println(c.getBookCategory().getBookTitle());
				}
			}
		}
		int totalRate = 0, totalPerson = 0;
		for (BookRating br : book.getBookRating()) {
			totalRate += br.getBookRate();
			totalPerson++;
		}

		model.addAttribute("totalRate", totalRate);
		model.addAttribute("totalPerson", totalPerson);

		// single clicked book
		model.addAttribute("book", book);
		// same category books
		model.addAttribute("alsoLikeBookList", bookList);
		System.out.println("booklist size : " + bookList.size());

		model.addAttribute("navbar", "none");
		StringOperations strOp = new StringOperations();
		model.addAttribute("strOp", strOp);
		return "BookInfo";
	}

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
			HttpSession session) {

		if (userRepository.findByUserEmail(user.getUserEmail()) != null) {
			model.addAttribute("user", user);
			model.addAttribute("tagName", "register");
			model.addAttribute("navbar", "bg-fyellow");
			model.addAttribute("alert", "email-exist");
			return "loginRegister";
		}

		try {
			if (result.hasErrors()) {
				model.addAttribute("user", user);
				model.addAttribute("tagName", "register");
				model.addAttribute("navbar", "bg-fyellow");
				model.addAttribute("alert", "none");
				return "loginRegister";
			}
			user.setUserPic("default.png");
			user.setRole("ROLE_USER");
			user.setUserPassword(encPassword.encode(user.getUserPassword()));
			String verificationString = RandomString.make();
			user.setVerificationCode(verificationString);
			user.setEnabled(false);
			userRepository.save(user);
			System.out.println("email sendar " + emailSender);
			Boolean m = new EmailActions().sendVerificationMail(user, verificationString, emailSender);
			System.out.println(user.getUserId() + " is saved !! message " + m);
			model.addAttribute("user", new User());
			model.addAttribute("tagName", "login");
			model.addAttribute("message", "Register");
			model.addAttribute("alert", "verify");
			return "loginRegister";

		} catch (Exception e) {
			e.printStackTrace();

			// TODO: handle exception
		}
		return "loginRegister";
	}// end registration method

	@RequestMapping("/verify")
	public String doVerify(@Param("code") String code, Model model) {
		User u = userRepository.findByVerificationCode(code);
		boolean f = false;
		if (u != null) {
			u.setEnabled(true);
			userRepository.save(u);
			f = true;
		}
		model.addAttribute("user", new User());
		model.addAttribute("loginUser", "none");
		model.addAttribute("loginUser", new User());
		model.addAttribute("tagName", "login");
		model.addAttribute("navbar", "bg-fyellow");
		model.addAttribute("message", "Register");
		if (f)
			model.addAttribute("alert", "sverify");
		else
			model.addAttribute("alert", "everify");
		return "loginRegister";
	}

	@RequestMapping("/allBooks/{page}")
	public String getBrowsBookPanel(@PathVariable("page") Integer page, Model model) {
		Pageable pageble = PageRequest.of(page, 8);
		Page<Book> pagebleBooks = bookRepository.findByStoreIdINotAndBookQuantityGreaterThanAndSaleTrue(0, 0, pageble);
		/*
		 * List<Book> books = new ArrayList<>(); for (Book book : pagebleBooks) { if
		 * (bookStoreRepository.getCountOfStore(book.getSeller()) > 0 &&
		 * book.getSeller().getBookStore().isCheckByAdmin() && book.isSale())//
		 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% books.add(book); if (books.size() == 8)
		 * break; } // int totalPage = Math.round(bookRepository.findAll().size() / 8) +
		 * 1; int totalPage = Math.round(books.size() / 8) + 1;
		 */
		/*
		 * System.out.println("Toataal Page : " + totalPage);
		 * System.out.println("Books : " + books.size());
		 */
		model.addAttribute("title", "All books");
		model.addAttribute("books", pagebleBooks);
		model.addAttribute("currentPage", page);
		model.addAttribute("navbar", "bg-fyellow");
		model.addAttribute("totalPage", pagebleBooks.getTotalPages());
		return "allBooks";
	}

	@RequestMapping("/searchMainBook/{page}/{name}")
	public String searchBook(@PathVariable int page, @PathVariable String name, Model model) {
		Pageable pageble = PageRequest.of(page, 8);
		Page<Book> pagebleBooks = bookRepository
				.findByStoreIdIsNotAndBookQuantityGreaterThanAndBookTitleContainingAndSaleTrue(0, 0, name, pageble);
		model.addAttribute("title", "All books");
		model.addAttribute("books", pagebleBooks);
		model.addAttribute("currentPage", page);
		model.addAttribute("navbar", "bg-fyellow");
		model.addAttribute("totalPage", pagebleBooks.getTotalPages());
		return "allBooks";
	}

	@RequestMapping("/doFindBook")
	public String findBook(@RequestParam("bName") String bname, @RequestParam("categorys") String categorys,
			@RequestParam("aName") String aname, @RequestParam("price") String price, @RequestParam("type") String type,
			@RequestParam("language") String language, Model model, HttpSession session) {
		System.out
				.println("Book Name : " + bname + " cat : " + categorys + " Auther  : " + aname + " price : " + price);
		List<Book> books = new ArrayList<>();
		/*
		 * session.setAttribute("bname", bname); session.setAttribute("aname", aname);
		 * session.setAttribute("price", price);
		 */
		try {

			if (aname == "" && bname == "") {
				System.out.println("ALL Book wise");
				books = bookRepository.findAll();
			} else if (aname != "" && bname == "") {
				System.out.println("ALL auther wise");
				books = bookRepository.getLikeAutherName(aname.trim());
			} else if (aname == "" && bname != "") {

				System.out.println("Book name wise");
				// System.out.println(bookRepository.findByBookTitleLike("%" + bname + "%"));
				books = bookRepository.getLikeBookName(bname.trim());
			} else {
				System.out.println(" Book Auther Both wise");
				books = bookRepository.getLikeAutherNameAndBookName(aname, bname);
			}

			System.out.println(books.size());
			if (!price.equals("all")) {

				for (Book b : books) {
					if (price.equals("less 200")) {
						System.out.println("less 200");
						if ((b.getBookPrice() - (b.getBookPrice() * b.getBookDiscount() / 100)) > 200) {
							books = Helper.removeFormBookList(books, b.getBookId());
						}
					} else if (price.equals("bet 200 to 500")) {
						System.out.println("bet 200 to 500");
						if ((b.getBookPrice() - (b.getBookPrice() * b.getBookDiscount() / 100)) < 200
								|| (b.getBookPrice() - (b.getBookPrice() * b.getBookDiscount() / 100)) > 500) {
							books = Helper.removeFormBookList(books, b.getBookId());
						}
					} else {
						System.out.println("grter 500");
						if ((b.getBookPrice() - (b.getBookPrice() * b.getBookDiscount() / 100)) < 500) {
							books = Helper.removeFormBookList(books, b.getBookId());
						}
					}
				}
			}
			System.out.println(categorys);
			if (categorys != "") {
				for (String cat : categorys.split(",")) {
					for (Book b : books) {
						boolean flag = true;
						for (BookCategorys bc : b.getCategory()) {
							System.out.println(cat + "  :  " + bc.getCategoryName());
							if (bc.getCategoryName().trim().equalsIgnoreCase(cat.trim()))
								flag = false;
						}
						if (flag)
							books = Helper.removeFormBookList(books, b.getBookId());

					}
				}
			}

			if (!type.equals("all")) {
				for (Book b : books) {
					if (!b.getBookType().equalsIgnoreCase(type))
						books = Helper.removeFormBookList(books, b.getBookId());
				}
			}

			if (!language.equals("all")) {
				for (Book b : books) {
					if (!b.getBookLanguage().equalsIgnoreCase(language))
						books = Helper.removeFormBookList(books, b.getBookId());
				}
			}
			System.out.println(books.size());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		List<Book> bookList = new ArrayList<>();
		for (Book book : books) {
			if (bookStoreRepository.getCountOfStore(book.getSeller()) > 0
					&& book.getSeller().getBookStore().isCheckByAdmin() && book.isSale())
				bookList.add(book);
		}

		int totalPage = Math.round(bookRepository.findAll().size() / 8) + 3;
		model.addAttribute("title", "All books");
		model.addAttribute("books", bookList);
		model.addAttribute("currentPage", 0);
		model.addAttribute("navbar", "bg-fyellow");
		if (bookList.size() == 0)
			model.addAttribute("totalPage", 0);
		else
			model.addAttribute("totalPage", 1);
		return "allBooks";
	}

	@RequestMapping("searchBook/{bname}")
	public String getSearchBookVieName(@PathVariable("bname") String bname, Model model) {

		List<Book> b = bookRepository.getLikeBookName(bname);
		List<Book> books = new ArrayList<>();
		// only store books present
		for (Book book : b) {
			if (bookStoreRepository.getCountOfStore(book.getSeller()) > 0
					&& book.getSeller().getBookStore().isCheckByAdmin() && book.isSale())
				books.add(book);
		}
		System.out.println("Toataal Page : " + 1);
		model.addAttribute("title", "search books");
		model.addAttribute("books", books);
		System.out.println(" bookRepository.findAll().size() " + bookRepository.findAll().size());
		model.addAttribute("currentPage", 0);
		model.addAttribute("totalPage", 1);
		model.addAttribute("navbar", "bg-fyellow");
		return "allBooks";
	}

	@RequestMapping("/resetPassword")
	public String resetPasswordPage(Model model) {
		model.addAttribute("navbar", "bg-fyellow");
		model.addAttribute("msg", "none");
		model.addAttribute("code", "none");
		return "resetPassword";
	}

	@PostMapping("/sendResetPasswordEmail")
	public String sendResetPasswordEmail(@RequestParam("email") String email, Model model) {
		User user = userRepository.findByUserEmail(email);
		model.addAttribute("navbar", "bg-fyellow");
		model.addAttribute("msg", "none");
		if (user == null) {
			model.addAttribute("msg", "invalidEmail");
		} else {
			boolean flag = EmailActions.sendRsetPasswordEmailMessage(user, user.getUserPassword(), emailSender);
			System.out.println("reset password email : " + flag);
			model.addAttribute("msg", "sendEmail");
		}

		model.addAttribute("code", "none");
		return "resetPassword";
	}

	@RequestMapping("/resetPasswordcodeVerification")
	public String resetPassword(@RequestParam("code") String code, Model model) {
		User user = userRepository.findByUserPassword(code);
		if (user == null) {
			model.addAttribute("code", "none");
			model.addAttribute("msg", "wrong");
		} else {
			model.addAttribute("msg", "password");
			model.addAttribute("code", code);
		}
		System.out.println("Reset password code : " + code);
		model.addAttribute("navbar", "bg-fyellow");

		return "resetPassword";
	}

	/*
	 * @PostMapping("/saveRatings")
	 * 
	 * @ResponseBody public String saveRatings(@RequestBody Map<String, Object> map)
	 * { int bookId = Integer.parseInt(map.get("bid").toString().trim()); int rate =
	 * Integer.parseInt(map.get("rate").toString().trim()); Book book =
	 * bookRepository.getById(bookId); System.out.println(book.getBookRating()); if
	 * (bookRepository.getById(bookId).getBookRating() == null) { BookRating brating
	 * = new BookRating(); brating.setTotalPerson(1); brating.setTotalRate(rate);
	 * brating.setBook(book); book.setBookRating(brating);
	 * bookRepository.save(book); } else { BookRating bookRating =
	 * book.getBookRating(); bookRating.setTotalPerson(bookRating.getTotalPerson() +
	 * 1); bookRating.setTotalRate(bookRating.getTotalRate() + rate);
	 * bookRatingRepository.save(bookRating); } JSONObject options = new
	 * JSONObject(); options.put("status", "success"); System.out.println("bid : " +
	 * bookId + " rate:  " + rate); return options.toString(); }
	 */
	@RequestMapping("/feedbacks/{page}")
	public String feedbackPage(@PathVariable("page") int page, Model model) {
		model.addAttribute("navbar", "bg-fyellow");
		Pageable pageable = PageRequest.of(page, 10, Sort.by("date").ascending());
		Page<UserFeedback> fpage = feedbackRepository.findAll(pageable);
		model.addAttribute("feedback", new UserFeedback());
		model.addAttribute("alert", "none");
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", fpage.getTotalPages());
		model.addAttribute("userfeedbacks", fpage);
		return "feedback";
	}

}
