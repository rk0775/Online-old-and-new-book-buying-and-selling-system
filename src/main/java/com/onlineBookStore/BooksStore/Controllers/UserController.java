package com.onlineBookStore.BooksStore.Controllers;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.onlineBookStore.BooksStore.Entities.Book;
import com.onlineBookStore.BooksStore.Entities.BookCategorys;
import com.onlineBookStore.BooksStore.Entities.BookOrder;
import com.onlineBookStore.BooksStore.Entities.BookRating;
import com.onlineBookStore.BooksStore.Entities.BookStore;
import com.onlineBookStore.BooksStore.Entities.FavoriteBooks;
import com.onlineBookStore.BooksStore.Entities.OrderPackage;
import com.onlineBookStore.BooksStore.Entities.User;
import com.onlineBookStore.BooksStore.Entities.UserFeedback;
import com.onlineBookStore.BooksStore.HelperClasses.Helper;
import com.onlineBookStore.BooksStore.HelperClasses.ImageDelete;
import com.onlineBookStore.BooksStore.HelperClasses.ImageSaver;
import com.onlineBookStore.BooksStore.HelperClasses.StringOperations;
import com.onlineBookStore.BooksStore.repository.BookCategoryRepository;
import com.onlineBookStore.BooksStore.repository.BookOrderRepository;
import com.onlineBookStore.BooksStore.repository.BookRatingRepository;
import com.onlineBookStore.BooksStore.repository.BookRepository;
import com.onlineBookStore.BooksStore.repository.BookStoreRepository;
import com.onlineBookStore.BooksStore.repository.FavoritebookRepository;
import com.onlineBookStore.BooksStore.repository.FeedbackRepository;
import com.onlineBookStore.BooksStore.repository.OrderPackageRepository;
import com.onlineBookStore.BooksStore.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	BookRepository bookRepository;
	@Autowired
	UserRepository userRepository;
	User user;
	@Autowired
	BookOrderRepository bookOrderRepository;
	@Autowired
	BookStoreRepository bookStoreRepository;
	@Autowired
	OrderPackageRepository orderPackageRepository;
	@Autowired
	BookCategoryRepository bookCategoryRepository;
	@Autowired
	BookRatingRepository bookRatingRepository;
	@Autowired
	FeedbackRepository feedbackRepository;
	@Autowired
	FavoritebookRepository favoritebookRepository;

	@ModelAttribute
	public void getData(Principal p, Model model) {
		Helper helper = new Helper(bookRatingRepository);
		model.addAttribute("h", helper);
		user = userRepository.findByUserEmail(p.getName());
		if (user.getRole().equalsIgnoreCase("ROLE_NORMAL_ADMIN")) {
			model.addAttribute("path", "/admin/adminDashboard");
		} else
			model.addAttribute("path", "/user/browseBooks/all,0");
		model.addAttribute("loginUser", user);
		model.addAttribute("fname", StringOperations.getOnlyUserFirstName(user.getUserName()));

	}

	@RequestMapping("/browseBooks/{action}")
	public String getBrowsBookPanel(@PathVariable("action") String actionUrl, Model model) {
		String a[] = actionUrl.trim().split(",");
		String action = a[0].trim();
		int page = Integer.parseInt(a[1].trim().toString());

		Pageable pageble = PageRequest.of(page, 8);
		Page<Book> pagebleBooks;
		// List<Book> books = new ArrayList<>();
		// only store books present\
		if (action.equals("all")) {
			pagebleBooks = bookRepository.findByStoreIdIsNotAndBookQuantityGreaterThanAndSaleTrue(0, 0, pageble);
			/*
			 * for (Book book : pagebleBooks) { if
			 * (bookStoreRepository.getCountOfStore(book.getSeller()) > 0 &&
			 * book.getSeller().getBookStore().isCheckByAdmin() && book.isSale())
			 * books.add(book); if (books.size() == 8) break; }
			 */
		} else {
			pagebleBooks = bookRepository.findByStoreIdIsNotAndBookQuantityGreaterThanAndBookLanguageAndSaleTrue(0, 0,
					action, pageble);
			
		}
		
		model.addAttribute("action", action);
		model.addAttribute("title", "Browse books");
		model.addAttribute("books", pagebleBooks);
		System.out.println(" bookRepository.findAll().size() " + bookRepository.findAll().size());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", pagebleBooks.getTotalPages());
		// model.addAttribute("showPagination", true);
		return "user/browseBooks";
	}

	@RequestMapping("/searchBookByName/{page}/{name}")
	public String searchBookByName(@PathVariable int page, @PathVariable("name") String name, Model model) {
		System.out.println("page is : " + page + " book name is : " + name);
		Pageable pageble = PageRequest.of(page, 8);
		Page<Book> pagebleBooks = bookRepository
				.findByStoreIdIsNotAndBookQuantityGreaterThanAndBookTitleContainingAndSaleTrue(0, 0, name, pageble);
		model.addAttribute("title", "Browse books");
		model.addAttribute("books", pagebleBooks);
		System.out.println(" bookRepository.findAll().size() " + bookRepository.findAll().size());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", pagebleBooks.getTotalPages());
		return "user/browseBooks";
	}

	@RequestMapping("/favorite_Books")
	public String getLoginUserFavoriteBooks(Model model) {
		model.addAttribute("title", "Favorite books");
		List<Book> books = new ArrayList<>();
		for (FavoriteBooks fb : user.getFavoiteBooks())
			books.add(fb.getFavoriteBook());
		model.addAttribute("books", books);
		return "user/favoriteBooks";
	}

	@RequestMapping("/Buying_history")
	public String getLoginUserBuyingHistory(Model model) {
		model.addAttribute("title", "Buying History Books");

		model.addAttribute("orders", user.getUserPlaceOrders());

		return "user/BuyingHistory";
	}

	@RequestMapping("/addBookForm")
	public String addBookForm(Model model) {
		model.addAttribute("title", "Add new Book");
		model.addAttribute("book", new Book());
		model.addAttribute("alert", "none");

		return "user/addBookForm";
	}

	@RequestMapping("/BuyOldBook/{page}")
	public String buyOldBookFormSeller(Model model, @PathVariable("page") Integer page) {
		Pageable pageble = PageRequest.of(page, 4);
		System.out.println("page " + page);
		Page<Book> pagebleBooks = bookRepository.findByStoreIdIsAndBookQuantityGreaterThanAndSaleTrue(0, 0, pageble);
		

		System.out.println("ok page " + page);
		String contactInfo = user.getUserAddr() + " Dist. " + user.getUserDistrict() + " Pincode : "
				+ user.getUserPincode() + " Contact : " + user.getUserPhone();
		model.addAttribute("title", "old books from students");
		model.addAttribute("books", pagebleBooks);

		model.addAttribute("contactInfo", contactInfo);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", pagebleBooks.getTotalPages());
		return "/user/BuyOldBook";
	}

	@RequestMapping("/searchOldBook/{page}/{name}")
	public String serachOldBookByName(@PathVariable int page, @PathVariable String name, Model model) {
		Pageable pageble = PageRequest.of(page, 32);
		System.out.println("page " + page);
		Page<Book> pagebleBooks = bookRepository
				.findByStoreIdIsAndBookQuantityGreaterThanAndBookTitleContainingAndSaleTrue(0, 0, name, pageble);
		String contactInfo = user.getUserAddr() + " Dist. " + user.getUserDistrict() + " Pincode : "
				+ user.getUserPincode() + " Contact : " + user.getUserPhone();
		model.addAttribute("title", "old books from students");
		model.addAttribute("books", pagebleBooks);

		model.addAttribute("contactInfo", contactInfo);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", pagebleBooks.getTotalPages());
		return "/user/BuyOldBook";
	}

	@RequestMapping("/do_storeBook")
	public String saveTheBookDetails(@Valid @ModelAttribute("book") Book book, BindingResult result,
			@RequestParam("bookCoverImg") MultipartFile file, @RequestParam("categorys") String categorys, Principal p,
			Model model) {

		if (result.hasErrors()) {
			model.addAttribute("title", "Add new Book");
			model.addAttribute("book", book);
			model.addAttribute("alert", "none");
			System.out.println("Error generated");
			return "user/addBookForm";
		}
		
		try {

			Random random = new Random();
			if (!file.isEmpty()) {
				String imgName = "bookCover_" + random.nextInt(100000) + file.getOriginalFilename();
				book.setBookCover(imgName);
				boolean flag = ImageSaver.save(file, "static/image/webContent/BookCoverImages", imgName);
				System.out.println(flag ? "Image is save" : "Image is not save ");
				model.addAttribute("imageSave", flag);
			} else {
				book.setBookCover("default.png");
			}
			// book add to user list of book
			User user = userRepository.findByUserEmail(p.getName());
			user.getBooks().add(book);
			// set the book seller here
			book.setSeller(user);

			if (bookStoreRepository.getCountOfStore(book.getSeller()) > 0 && user.getBookStore().isCheckByAdmin())
				book.setStoreId(book.getSeller().getBookStore().getStoreId());

			book.setSale(true);
			/*****************************************************/
			// here we save the category
			String c[] = categorys.split(",");
			List<BookCategorys> categoryList = new ArrayList<>();
			for (String category : c) {
				BookCategorys bookCategorys = new BookCategorys();
				bookCategorys.setBookCategory(book);
				// set book category is ManyToOne relation one book has many
				// category so provide the list of category to book class
				bookCategorys.setCategoryName(category);
				// first all we save the catgory details in table
				categoryList.add(bookCategorys);
			}
			book.setCategory(categoryList);
			/*****************************************************/
			userRepository.save(user);
			model.addAttribute("alert", "saveBook");
			model.addAttribute("book", new Book());

		} catch (Exception e) {
			model.addAttribute("alert", "bookNotSave");
			e.printStackTrace();
			// TODO: handle exception
		}

		return "user/addBookForm";
	}

	@RequestMapping("/myStore")
	public String StoreRegistrationPanel(Model model) throws ParseException {
		if (user.getBookStore() == null) {
			model.addAttribute("store", "none");
		} else if (user.getBookStore().isCheckByAdmin() && user.getBookStore().isValidity()) {

			BookStore bs = user.getBookStore();
			model.addAttribute("store", bs);
			LocalDate date = LocalDate.now();
			System.out.println("Local date : " + date);
			System.out.println("Local date : " + bs.getEnddate());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			Date firstDate = sdf.parse(date.toString());
			Date secondDate = sdf.parse(bs.getEnddate().toString());
			long diff = secondDate.getTime() - firstDate.getTime();
			TimeUnit time = TimeUnit.DAYS;
			long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);
			System.out.println("The difference in days is : " + diffrence);
			model.addAttribute("store", bs);
			model.addAttribute("day", diffrence);
			if (diffrence <= 3) {
				model.addAttribute("timeMsg", "true");
			}
			if (date == bs.getEnddate()) {
				bs.setValidity(false);
				bookStoreRepository.save(bs);
			}

		} else {
			model.addAttribute("store", "none");
			if (user.getBookStore().isCheckByAdmin() == false && user.getBookStore().isValidity())
				model.addAttribute("timeMsg", "wait");
			else
				model.addAttribute("timeMsg", "invalid");// invalid msg
		}
		System.out.println(user.getBookStore());
		return "user/myStore";
	}

	@RequestMapping("/storeRegistrationForm/{offerNumber}")
	public String showStoreRegistrationForm(@PathVariable("offerNumber") Integer offer, Model model) {
		model.addAttribute("validity", offer);
		int amountOfrent = 0;
		if (offer == 1)
			amountOfrent = 39;
		else if (offer == 2)
			amountOfrent = 149;
		model.addAttribute("rzKey", "rzp_test123");// Key for payment reference admin key
		model.addAttribute("user", user);
		model.addAttribute("amount", amountOfrent);
		model.addAttribute("bookStore", new BookStore());
		return "user/addStoreForm";
	}

	@PostMapping("/SaveStoreDetailes")
	@ResponseBody
	public String saveStoreData(Model model, Principal p, @RequestBody Map<String, Object> map) {

		User user = userRepository.findByUserEmail(p.getName());
		BookStore bookStore = new BookStore();

		int offer = Integer.parseInt(map.get("validyType").toString());

		bookStore.setDelivery(Boolean.parseBoolean(map.get("deliveryFaci").toString()));
		LocalDate date = LocalDate.now();
		bookStore.setStartdate(date);
		if (offer == 1) {
			System.out.println("one here");
			bookStore.setEnddate(date.plusMonths(1));
		} else if (offer == 2) {
			System.out.println("2 here");
			bookStore.setEnddate(date.plusMonths(3));
		} else if (offer == 3) {
			System.out.println("3 here");
			bookStore.setEnddate(date.plusMonths(6));
		} else if (offer == 4) {
			System.out.println("4 here");
			bookStore.setEnddate(date.plusYears(1));
		}

		bookStore.setStoreName(map.get("storeName").toString());
		bookStore.setStoreDis(map.get("storeDis").toString());
		bookStore.setStoreTime(map.get("storeTime").toString());
		bookStore.setStoreAddr(map.get("storeAddr").toString());
		Boolean onlineService = Boolean.parseBoolean(map.get("onlineBuy").toString());
		System.out.println(onlineService + " " + map.get("key").toString() + " " + map.get("secret"));
		if (onlineService) {
			bookStore.setOnlinePayment(onlineService);
			bookStore.setRZkeyId(map.get("key").toString());
			bookStore.setRZkeySecret(map.get("secret").toString());
		} else
			bookStore.setOnlinePayment(false);
		bookStore.setStorePic("default.png");
		bookStore.setValidity(true);
		bookStore.setCheckByAdmin(false);
		user.setBookStore(bookStore);
		userRepository.save(user);
		// System.out.println("file " + file.getAbsolutePath());
		System.out.println("book store deteails   " + bookStore);
		JSONObject options = new JSONObject();
		options.put("status", "success");
		return options.toString();
	}

	@PostMapping("/do_paymentOfStore")
	@ResponseBody
	public String saveStore(@RequestBody Map<String, Object> map) throws Exception {
		System.out.println("here");
		System.out.println(map);
		int amount = 0;
		int offerCardNumber = Integer.parseInt(map.get("validyType").toString());

		if (offerCardNumber == 1)
			amount = 39;
		else if (offerCardNumber == 2)
			amount = 149;
		else if (offerCardNumber == 3)
			amount = 259;
		else if (offerCardNumber == 4)
			amount = 449;

		RazorpayClient client = new RazorpayClient("rzp_id123", "Rzp_key123");

		JSONObject options = new JSONObject();
		options.put("amount", amount * 100);
		options.put("currency", "INR");
		options.put("receipt", "txn_101010");
		Order order = client.Orders.create(options);

		return order.toString();
	}

	@RequestMapping("/chekout")
	public String checkoutPage(Model model, Principal p) {
		String addr = user.getUserAddr() + " " + user.getUserDistrict() + " " + user.getUserPincode();
		model.addAttribute("navbar", "bg-fyellow");
		model.addAttribute("title", "checkout here");
		model.addAttribute("rzKey", "rzp_test123");// payment for admin
		model.addAttribute("oldAddr", addr);
		model.addAttribute("loginUser", user);
		model.addAttribute("fname", StringOperations.getOnlyUserFirstName(user.getUserName()));
		// user=userRepository.findByUserEmail(p.getName());
		return "user/checkoutPage";
	}

	@PostMapping("/do_paymentOfBook")
	@ResponseBody
	public String paymentOfBook(@RequestBody Map<String, Object> map) throws Exception {
		int amt = 0;
		ArrayList<String> bookIds = (ArrayList<String>) map.get("OrderdBookIds");

		ArrayList<Integer> bookQuntitys = (ArrayList<Integer>) map.get("bookQuntity");

		for (int i = 0; i < bookIds.size(); i++) {
			String id = bookIds.get(i);
			int qun = bookQuntitys.get(i);
			Book book = bookRepository.getById(Integer.parseInt(id));
			System.out.println(qun + " book id : " + id); //
			if (book.getBookQuantity() < qun) {
				JSONObject errors = new JSONObject();
				errors.put("status", "quntityLess");
				errors.put("bname", book.getBookTitle());
				errors.put("rqun", book.getBookQuantity());
				return errors.toString();
			}

			// System.out.println(bookQuntitys.get(i));
			amt += ((book.getBookPrice() - ((book.getBookPrice() * book.getBookDiscount()) / 100))
					* bookQuntitys.get(i));
		}
		System.out.println(amt);
		// payment to admin
		RazorpayClient client = new RazorpayClient("rzp_test123", "rzp_key123");

		JSONObject options = new JSONObject();
		options.put("amount", amt * 100);
		options.put("currency", "INR");
		options.put("receipt", "txn_101010");
		Order order = client.Orders.create(options);
		return order.toString();
	}

	@PostMapping("/SaveBookOrder")
	@ResponseBody
	public String SaveTheBookOrder(Principal p, @RequestBody Map<String, Object> map) throws Exception {
		System.out.println(map);
		System.out.println("I here");

		Boolean changeAddrFlag = Boolean.parseBoolean(map.get("changeAddrFlag").toString());
		String newAddr = map.get("newAddr").toString();
		String newPincode = map.get("newPincode").toString();
		String newDistrict = map.get("newDistrict").toString();
		String userName = map.get("userName").toString();
		String newPhoneNum = map.get("newPhoneNum").toString();
		// String payType = map.get("payType").toString();
		ArrayList<String> bookIds = (ArrayList<String>) map.get("OrderdBookIds");
		System.out.println("Book id size is  : " + bookIds.size());
		ArrayList<Integer> bookQuntitys = (ArrayList<Integer>) map.get("bookQuntity");
		for (int i = 0; i < bookIds.size(); i++) {
			String id = bookIds.get(i);
			BookOrder bookOrder = new BookOrder();
			Book book = bookRepository.getById(Integer.parseInt(id));
			System.out.println("Book Name Is  : " + book.getBookTitle());

			if (bookStoreRepository.getCountOfStore(book.getSeller()) > 0) {
				BookStore bookStore = book.getSeller().getBookStore();
				bookStore.setPlaceOrderPrice(bookStore.getPlaceOrderPrice() + book.getBookPrice());
				bookStoreRepository.save(bookStore);
			}
			System.out.println("new phone" + newPhoneNum);
			if (newPhoneNum.equals(""))
				bookOrder.setOrderContactNum(user.getUserPhone());
			else
				bookOrder.setOrderContactNum(newPhoneNum);

			bookOrder.setOrderDate(LocalDate.now());
			bookOrder.setOrderedBooks(book);
			bookOrder.setOrderPersonName(userName);

			bookOrder.setOrderStatus("admin");
			if (changeAddrFlag) {
				bookOrder.setOderAddr(newAddr);
				bookOrder.setOrderDistrict(newDistrict);
				bookOrder.setOrderPincode(newPincode);
			} else {
				bookOrder.setOderAddr(user.getUserAddr());
				bookOrder.setOrderDistrict(user.getUserDistrict());
				bookOrder.setOrderPincode(user.getUserPincode());
			}
			// Payment type
			bookOrder.setPaymentType("online");
			bookOrder.setBookQuantity(bookQuntitys.get(i));
			bookOrder.setOrderPrice((book.getBookPrice() - ((book.getBookPrice() * book.getBookDiscount()) / 100))
					* (bookQuntitys.get(i)));

			bookOrder.setOrderedUser(user);
			user.getUserPlaceOrders().add(bookOrder);
			int qun = book.getBookQuantity() - (bookQuntitys.get(i));
			book.setBookQuantity(qun);
			book.getBookOrders().add(bookOrder);
			// userRepository.save(user);
			// bookRepository.save(book);
			bookOrderRepository.save(bookOrder);
			System.out.println("Book order :  " + bookOrder);
		}
		JSONObject options = new JSONObject();
		options.put("status", "success");
		return options.toString();
	}

	// My books
	@RequestMapping("/myBooks/{page}")
	public String myBooks(Model model, @PathVariable("page") int page) {

		Pageable pageble = PageRequest.of(page, 8);
		Page<Book> pagebleBooks = bookRepository.findAllPageByUserId(user.getUserId(), pageble);
		model.addAttribute("books", pagebleBooks);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", pagebleBooks.getTotalPages());
		model.addAttribute("bookValidation", true);
		return "/user/MyBooks";
	}

	// cash on delivery
	@PostMapping("/orderTheOldBook")
	@ResponseBody
	public String SaveOldBookOrder(@RequestBody Map<String, Object> map) throws Exception {
		int qun = Integer.parseInt(map.get("qun").toString());
		System.out.println(map);
		Book book = bookRepository.findByBookId(Integer.parseInt(map.get("id").toString()));
		JSONObject options = new JSONObject();
		int bookQuntitys = book.getBookQuantity() - qun;
		if (bookQuntitys < 0) {
			options.put("status", "error");
			options.put("msg", "oops!!  only " + book.getBookQuantity() + " book quntity remaining ");
			return options.toString();
		} else {

			BookOrder order = new BookOrder();
			order.setOrderDate(LocalDate.now());
			order.setBookQuantity(qun);// how many quntity they buy
			order.setOrderPersonName(user.getUserName());
			order.setOrderContactNum(user.getUserPhone());
			order.setOrderDistrict(user.getUserDistrict());
			order.setOrderPincode(user.getUserPincode());
			order.setOderAddr(user.getUserAddr());
			book.setBookQuantity(bookQuntitys);
			order.setOrderedUser(user);
			order.setOrderedBooks(book);
			order.setOrderStatus("store");
			order.setOrderPrice(book.getBookPrice());
			order.setPaymentType("cash on delivery");
			user.getUserPlaceOrders().add(order);
			book.getBookOrders().add(order);
			// userRepository.save(user);
			// bookRepository.save(book);
			bookOrderRepository.save(order);
			System.out.println(order);
			options.put("status", "done");
		}
		return options.toString();

	}

	@RequestMapping("/orderBookDetails/{bookId}")
	public String orederBooksDetails(Model model, @PathVariable("bookId") Integer bookId) {
		List<BookOrder> obs = new ArrayList<>();
		List<BookOrder> orders = bookOrderRepository.findByOrderedBooks(bookRepository.getById(bookId));
		for (BookOrder order : orders) {
			if (order != null)
				obs.add(order);
		}
		model.addAttribute("orders", obs);
		return "user/OrderBooksDetails";
	}

	@RequestMapping("/placeOrder/{orderId}")
	public String orderIsbnForm(Model model, @PathVariable("orderId") Integer orderId) {
		BookOrder bookOrder = bookOrderRepository.findByOrderId(orderId);
		System.out.println(bookOrder);
		String personAddress = bookOrder.getOrderedUser().getUserAddr() + " Dis. "
				+ bookOrder.getOrderedUser().getUserDistrict() + " Pincode : "
				+ bookOrder.getOrderedUser().getUserPincode();
		String shippingAddress = bookOrder.getOderAddr() + " Dis. " + bookOrder.getOrderDistrict() + " Pincode : "
				+ bookOrder.getOrderPincode();

		List<OrderPackage> packages = orderPackageRepository.findByOrder(bookOrder);
		model.addAttribute("order", bookOrder);
		model.addAttribute("pAddr", personAddress);
		model.addAttribute("sAddr", shippingAddress);
		model.addAttribute("packages", packages);
		model.addAttribute("page", bookOrder.getOrderedBooks().getBookId());
		System.out.println(" Size :::  " + packages.size());
		return "user/placeOrder";
	}

	@RequestMapping("/myOrders")
	public String showLoginUserOrderdBooks(Model model) {
		model.addAttribute("orders", user.getUserPlaceOrders());
		model.addAttribute("msg", "none");
		return "/user/MyOrders";
	}

	@PostMapping("/savePackageDetails")
	@ResponseBody
	public String savePackageDetails(@RequestBody Map<String, Object> map) {
		ArrayList<String> list = (ArrayList<String>) map.get("arr");
		BookOrder order = bookOrderRepository.findByOrderId(Integer.parseInt(map.get("oid").toString().trim()));
		System.out.println("map : " + map + "/n list" + list);
		if (!order.getOrderStatus().equals("cancel"))
			for (String isbn : list) {
				OrderPackage orderPackage = new OrderPackage();
				System.out.println("isbn : " + isbn);
				orderPackage.setOrder(order);
				orderPackage.setBookIsbnNumber(isbn);
				orderPackage.setDeliveryPerson(map.get("dBoy").toString());
				// orderPackageRepository.save(orderPackage);
				order.getOrderPackages().add(orderPackage);
				order.setOrderStatus("packed");
				bookOrderRepository.save(order);
			}
		return new JSONObject().put("status", "success").toString();
	}

	@PostMapping("/editPackageDetails")
	@ResponseBody
	public String editPackageDetails(@RequestBody Map<String, Object> map) {
		ArrayList<String> list = (ArrayList<String>) map.get("arr");
		ArrayList<String> ids = (ArrayList<String>) map.get("ids");
		BookOrder order = bookOrderRepository.findByOrderId(Integer.parseInt(map.get("oid").toString().trim()));
		if (!order.getOrderStatus().equals("cancel"))
			for (int i = 0; i < ids.size(); i++) {
				String isbn = list.get(i);
				int id = Integer.parseInt(ids.get(i));
				OrderPackage orderPackage = orderPackageRepository.findByOrderPackageId(id);
				orderPackage.setBookIsbnNumber(isbn);
				orderPackage.setDeliveryPerson(map.get("dBoyz").toString().trim());
				orderPackageRepository.save(orderPackage);
			}
		return new JSONObject().put("status", "success").toString();
	}

	@RequestMapping("/cancelOrder/{orderId}")
	public String cancelTheOrder(@PathVariable("orderId") int oid, Model model) {
		BookOrder order = bookOrderRepository.findByOrderId(oid);
		Book book = bookRepository.findByBookId(order.getOrderedBooks().getBookId());
		order.setOrderStatus("cancel");
		book.setBookQuantity((book.getBookQuantity()) + order.getBookQuantity());
		// bookOrderRepository.save(order);
		model.addAttribute("msg", "success");
		bookRepository.save(book);
		if (bookStoreRepository.getCountOfStore(book.getSeller()) > 0) {
			BookStore bookStore = book.getSeller().getBookStore();
			bookStore.setPlaceOrderPrice(bookStore.getPlaceOrderPrice() - book.getBookPrice());
			bookStoreRepository.save(bookStore);
		}
		return "/user/MyOrders";
	}

	@RequestMapping("changeStatus/{orderId}")
	public String changeTheStatus(@PathVariable("orderId") int oid, Model model) {
		BookOrder order = bookOrderRepository.findByOrderId(oid);
		order.setOrderStatus("delivered");
		bookOrderRepository.save(order);

		List<BookOrder> obs = new ArrayList<>();
		List<BookOrder> orders = bookOrderRepository
				.findByOrderedBooks(bookRepository.getById(order.getOrderedBooks().getBookId()));
		for (BookOrder or : orders) {
			if (or != null)
				obs.add(order);
		}
		model.addAttribute("orders", obs);
		return "user/OrderBooksDetails";

	}

	// for show the specific book info
	@PostMapping("/addTofavorite/{bookId}")
	@ResponseBody
	public String singleBookinformation(@PathVariable("bookId") Integer bookId, Model model) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		Book book = bookRepository.findByBookId(bookId);
		JSONObject options = new JSONObject();
		FavoriteBooks fb = favoritebookRepository.findByFavoriteBookAndFavoriteUser(book, user);
		if (fb == null) {
			FavoriteBooks favoriteBooks = new FavoriteBooks();
			favoriteBooks.setFavoriteBook(book);
			favoriteBooks.setFavoriteUser(user);
			user.getFavoiteBooks().add(favoriteBooks);
		} else {
			options.put("status", "present");
			return options.toString();
		}
		userRepository.save(user);
		System.out.println("all done");
		options.put("status", "success");
		return options.toString();

	}

	@RequestMapping("/ckeckStoreOrder")
	public String checkTheLoginUserStoreOrders(Model model) {
		List<BookOrder> orders = new ArrayList<>();
		for (Book b : user.getBooks()) {

			List<BookOrder> list = bookOrderRepository.findByOrderedBooks(b);
			if (list.size() == 1 && (list.get(0).getOrderStatus().equals("store")
					|| list.get(0).getOrderStatus().equals("admin-store")))
				orders.add(list.get(0));
			// System.out.println("List Size : " + list.size());
		}
		model.addAttribute("orders", orders);
		return "/user/StoreShippingOrder";
	}

	@RequestMapping("/ckeckStoreOrder/{action}")
	public String checkTheLoginUserStoreOrders(Model model, @PathVariable("action") String action) {
		List<BookOrder> orders = new ArrayList<>();
		for (Book b : user.getBooks()) {

			List<BookOrder> list = bookOrderRepository.findByOrderedBooks(b);
			if (action.equals("uncomplete")) {
				if (list.size() == 1 && (list.get(0).getOrderStatus().equals("store")
						|| list.get(0).getOrderStatus().equals("admin-store")))
					orders.add(list.get(0));
			} else if (action.equals("complete")) {
				if (list.size() == 1 && (list.get(0).getOrderStatus().equals("packed")))
					orders.add(list.get(0));
			} else if (action.equals("all")) {
				if (list.size() == 1 && ((list.get(0).getOrderStatus().equals("packed"))
						|| (list.get(0).getOrderStatus().equals("store"))
						|| (list.get(0).getOrderStatus().equals("admin-store"))))
					orders.add(list.get(0));
			}
			// System.out.println("List Size : " + list.size());
		}
		model.addAttribute("orders", orders);
		return "/user/StoreShippingOrder";
	}

	@RequestMapping("/profile")
	public String userProfile() {
		return "/user/userProfile";
	}

	@RequestMapping("/editProfilePage")
	public String editProfilepage() {
		return "/user/userEditprofile";
	}

	@PostMapping("/do_edit")
	public String editUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
			@RequestParam("userpicture") MultipartFile file, HttpSession session) {

		try {
			if (result.hasErrors()) {
				model.addAttribute("user", user);

				return "/user/userEditprofile";
			}
			if (!file.isEmpty()) {
				if (!user.getUserPic().equals("default.png")) {
					boolean f = ImageDelete.delete("static/image/webContent/userImages", user.getUserPic());
					if (f)
						System.out.println("Old file deleted");
				}
				String fname = "User" + new Random(1000).nextInt() + file.getOriginalFilename();
				boolean flag = ImageSaver.save(file, "static/image/webContent/userImages", fname);
				if (flag == true)
					System.out.println("New Image will save");
				user.setUserPic(fname);
			}

			// user.setUserPassword(encPassword.encode(user.getUserPassword()));
			System.out.println(user);
			userRepository.save(user);
			// System.out.println(user.getUserId() + " is saved !!");
			return "/user/userEditprofile";

		} catch (Exception e) {
			e.printStackTrace();

			// TODO: handle exception
		}
		return "/user/userEditprofile";
	}// end registration method

	@RequestMapping("/editStorePage")
	public String editStoreForm(Model model) {
		BookStore store = user.getBookStore();
		model.addAttribute("store", store);
		model.addAttribute("msg", "none");
		return "/user/editStoreForm";
	}

	@PostMapping("/do_editStoreInfo")
	public String editStore(@Valid @ModelAttribute("store") BookStore store, BindingResult result, Model model,
			@RequestParam("storepicture") MultipartFile file, HttpSession session) {
		BookStore bookStore = user.getBookStore();
		try {
			if (result.hasErrors()) {
				model.addAttribute("store", store);

				return "/user/editStoreForm";
			}
			if (!file.isEmpty()) {
				if (!bookStore.getStorePic().equals("default.png")) {
					boolean f = ImageDelete.delete("static/image/webContent/storeImages", store.getStorePic());
					if (f)
						System.out.println("Old file deleted");
				}
				String fname = "Store" + new Random(1000).nextInt() + file.getOriginalFilename();
				boolean flag = ImageSaver.save(file, "static/image/webContent/storeImages", fname);
				if (flag == true)
					System.out.println("New Image will save");
				bookStore.setStorePic(fname);
			}

			// user.setUserPassword(encPassword.encode(user.getUserPassword()));

			bookStore.setStoreName(store.getStoreName());
			bookStore.setStoreDis(store.getStoreDis());
			bookStore.setStoreTime(store.getStoreTime());
			if (store.getRZkeyId() != null && !store.getRZkeyId().equals("")) {
				bookStore.setRZkeyId(store.getRZkeyId());
				bookStore.setRZkeySecret(store.getRZkeySecret());
			}
			System.out.println("razorpay : " + store.getRZkeyId());
			bookStore.setDelivery(store.isDelivery());
			bookStore.setStoreAddr(store.getStoreAddr());

			System.out.println(bookStore);
			bookStoreRepository.save(bookStore);
			// System.out.println(user.getUserId() + " is saved !!");
			model.addAttribute("msg", "done");
			model.addAttribute("store", bookStore);
			return "/user/editStoreForm";

		} catch (Exception e) {
			e.printStackTrace();

			// TODO: handle exception
		}
		return "/user/editStoreForm";
	}// end registration method

	@RequestMapping("/removeBook")
	@ResponseBody
	public String removeBook(@RequestBody Map<String, Object> map, Model model) {
		boolean flag = false;
		System.out.println("Bojhsdfhsdfhh");
		JSONObject options = new JSONObject();
		int bid = Integer.parseInt(map.get("bid").toString());
		for (Book b : user.getBooks()) {
			if (b.getBookId() == bid) {
				b.setSale(!b.isSale());
				bookRepository.save(b);
				flag = true;
			}
		}
		if (flag)
			options.put("status", "success");
		else
			options.put("status", "error");
		return options.toString();
	}

	@RequestMapping("/editBook/{bid} {page}")
	public String editBook(@PathVariable("bid") int bid, @PathVariable("page") int page, Model model) {
		boolean flag = false;
		for (Book b : user.getBooks()) {
			if (b.getBookId() == bid) {

				String cat = "";
				for (BookCategorys c : b.getCategory())
					cat += c.getCategoryName() + " , ";

				model.addAttribute("categorys", cat);
				model.addAttribute("book", b);
				flag = true;
			}
		}
		System.out.println("Page  : " + page);
		model.addAttribute("page", page);
		model.addAttribute("bookValidation", flag);

		return "/user/editBook";
	}

	@PostMapping("/do_editBookInfo")
	public String editUser(@Valid @ModelAttribute("book") Book book, BindingResult result, Model model,
			@RequestParam("bookpicture") MultipartFile file, @RequestParam("cat") String categorys,
			@RequestParam("page") String page, HttpSession session) {
		Book b = bookRepository.findByBookId(book.getBookId());
		try {
			if (result.hasErrors()) {
				model.addAttribute("book", book);
				return "/user/editBook";
			}
			if (!file.isEmpty()) {
				if (!b.getBookCover().equals("default.png")) {
					boolean f = ImageDelete.delete("static/image/webContent/BookCoverImages", b.getBookCover());
					if (f)
						System.out.println("Old file deleted");
				}
				String fname = "Book" + new Random(1000).nextInt() + file.getOriginalFilename();
				boolean flag = ImageSaver.save(file, "static/image/webContent/BookCoverImages", fname);
				if (flag == true)
					System.out.println("New Image will save");
				b.setBookCover(fname);
			}

			// user.setUserPassword(encPassword.encode(user.getUserPassword()));
			b.setBookTitle(book.getBookTitle());
			b.setBookAuthor(book.getBookAuthor());
			b.setBookPrice(book.getBookPrice());
			b.setBookDiscount(book.getBookDiscount());
			b.setBookQuantity(book.getBookQuantity());
			b.setBookDescription(book.getBookDescription());
			b.setBookLanguage(book.getBookLanguage());
			b.setBookType(book.getBookType());

			List<BookCategorys> cat = b.getCategory();
			b.setCategory(null);
			for (BookCategorys bc : cat) {
				bookCategoryRepository.deleteById(bc.getCategoryId());
				// System.out.println("Delete " + f);
			}
			/*****************************************************/
			// here we save the category
			String c[] = categorys.split(",");
			List<BookCategorys> categoryList = new ArrayList<>();
			for (String category : c) {
				if (!category.trim().equals("") && !(category.trim().length() < 1)) {
					System.out.println("Category name : " + category);
					BookCategorys bookCategorys = new BookCategorys();
					bookCategorys.setBookCategory(book);
					// set book category is ManyToOne relation one book has many
					// category so provide the list of category to book class
					bookCategorys.setCategoryName(category.trim());
					// first all we save the catgory details in table
					categoryList.add(bookCategorys);
				}
			}
			b.setCategory(categoryList);
			// System.out.println(b);
			bookRepository.save(b);
			model.addAttribute("book", b);
			model.addAttribute("bookValidation", true);
			model.addAttribute("categorys", categorys);
			model.addAttribute("page", page.trim());
			// System.out.println(user.getUserId() + " is saved !!");
			return "/user/editBook";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/user/editBook";
	}// end registration method

	@RequestMapping("/getpayment/{sid}")
	public String getPaymentFromAdmin(@PathVariable("sid") int sid, Model model) {
		BookStore bookStore = bookStoreRepository.findByStoreId(sid);
		if (user.getUserId() != sid) {
			return "/errorPage";
		}
		bookStore.setTotalEarnings(bookStore.getTotalEarnings() + bookStore.getPlaceOrderPrice());
		bookStore.setPlaceOrderPrice(0);
		bookStoreRepository.save(bookStore);
		return "/user/myStore";
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
		return "user/browseBooks";
	}

	@PostMapping("/saveRatings")
	@ResponseBody
	public String saveRatings(@RequestBody Map<String, Object> map) {
		System.out.println("_________________________");
		int bookId = Integer.parseInt(map.get("bid").toString().trim());
		int rate = Integer.parseInt(map.get("rate").toString().trim());
		Book book = bookRepository.getById(bookId);

		BookRating br = bookRatingRepository.findByRateuserAndBook(user, book);
		System.out.println("Br " + br);
		if (br == null) {
			BookRating bookRating = new BookRating();
			bookRating.setBookRate(rate);
			bookRating.setRateuser(user);
			bookRating.setBook(book);
			bookRating.setDate(LocalDate.now());
			book.getBookRating().add(bookRating);
			book.setBookRating(book.getBookRating());
			bookRepository.save(book);
		} else {
			br.setDate(LocalDate.now());
			br.setBookRate(rate);
			bookRatingRepository.save(br);
		}
		System.out.println("rating saved sccefully");
		JSONObject options = new JSONObject();
		options.put("status", "success");
		System.out.println("bid : " + bookId + " rate:  " + rate);
		return options.toString();
	}

	@PostMapping("/saveReview")
	@ResponseBody
	public String saveReview(@RequestBody Map<String, Object> map) {
		System.out.println("_________________________");
		int bookId = Integer.parseInt(map.get("bid").toString().trim());
		String review = map.get("review").toString().trim();
		Book book = bookRepository.getById(bookId);

		BookRating br = bookRatingRepository.findByRateuserAndBook(user, book);
		System.out.println("Br " + br);
		br.setReview(review);
		bookRatingRepository.save(br);
		System.out.println("review saved sccefully");
		JSONObject options = new JSONObject();
		options.put("status", "success");
		// System.out.println("bid : " + bookId + " rate: " + rate);
		return options.toString();

	}

	@PostMapping("/saveFeedBack/{page}")
	public String saveFeedback(@Valid @ModelAttribute("feedback") UserFeedback feedback, BindingResult result,
			@PathVariable("page") int page, Model model) {

		model.addAttribute("navbar", "bg-fyellow");
		if (result.hasErrors()) {
			model.addAttribute("alert", "error");
			model.addAttribute("feedback", feedback);
			return "feedback";
		}
		feedback.setFpuser(user);
		feedback.setDate(LocalDate.now());
		model.addAttribute("alert", "success");

		Pageable pageable = PageRequest.of(page, 10, Sort.by("date").ascending());
		Page<UserFeedback> fpage = feedbackRepository.findAll(pageable);
		model.addAttribute("feedback", new UserFeedback());

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", fpage.getTotalPages());
		model.addAttribute("userfeedbacks", fpage);

		return "feedback";
	}

}
