package com.onlineBookStore.BooksStore.Controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.onlineBookStore.BooksStore.Entities.BookOrder;
import com.onlineBookStore.BooksStore.Entities.BookRating;
import com.onlineBookStore.BooksStore.Entities.BookStore;
import com.onlineBookStore.BooksStore.Entities.User;
import com.onlineBookStore.BooksStore.Entities.UserFeedback;
import com.onlineBookStore.BooksStore.HelperClasses.EmailActions;
import com.onlineBookStore.BooksStore.HelperClasses.ImageDelete;
import com.onlineBookStore.BooksStore.HelperClasses.ImageSaver;
import com.onlineBookStore.BooksStore.repository.BookOrderRepository;
import com.onlineBookStore.BooksStore.repository.BookRepository;
import com.onlineBookStore.BooksStore.repository.BookStoreRepository;
import com.onlineBookStore.BooksStore.repository.FeedbackRepository;
import com.onlineBookStore.BooksStore.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;


@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	BookRepository bookRepository;
	@Autowired
	BookStoreRepository bookStoreRepository;
	User user;
	@Autowired
	FeedbackRepository feedbackRepository;
	@Autowired
	BCryptPasswordEncoder encPassword;

	@RequestMapping("/adminDashboard")
	public String adminDashboard(Model model) {
		model.addAttribute("totalBooks", bookRepository.count());
		model.addAttribute("newBooks", bookRepository.getCountByTypeBook("new"));
		model.addAttribute("oldBooks", bookRepository.getCountByTypeBook("old"));
		model.addAttribute("totalUsers", userRepository.count());
		model.addAttribute("totalStores", bookStoreRepository.count());
		model.addAttribute("totalOrders", bookOrderRepository.count());
		model.addAttribute("unCheckStore", bookStoreRepository.findByCheckByAdmin(false));
		model.addAttribute("unCheckOrder", bookOrderRepository.findByOrderStatus("admin", "store"));

		List<BookStore> bookStore = bookStoreRepository.findTop30ByOrderByStartdateDesc();
		List<BookStore> stores = new ArrayList<>();
		for (BookStore s : bookStore) {
			if (!s.isCheckByAdmin())
				stores.add(s);
			if (stores.size() == 10)
				break;
		}
		model.addAttribute("bookStores", stores);

		List<BookOrder> bookOrder = bookOrderRepository.findTop30ByOrderByOrderDateDesc();
		List<BookOrder> orders = new ArrayList<>();
		for (BookOrder o : bookOrder) {
			if (o.getOrderStatus().equals("admin"))
				orders.add(o);
			if (orders.size() == 10)
				break;
		}
		model.addAttribute("bookOrders", orders);

		return "/admin/frontPage";
	}

	@RequestMapping("/orderCheckout/{action}")
	public String OrderCheckoutActions(@PathVariable("action") String str, Model model) {
		String action = str.split(",")[0];
		int page = Integer.parseInt(str.split(",")[1]);
		System.out.println("action " + action + "  page : " + page);
		Pageable data = PageRequest.of(page, 8);

		if (action.trim().equals("all")) {
			Page<BookOrder> d = bookOrderRepository.findAll(data);
			model.addAttribute("orders", d);
			model.addAttribute("totalPage", d.getTotalPages());
			model.addAttribute("next", "all,");
		} else

		if (action.trim().equals("placed")) {

			Page<BookOrder> d = bookOrderRepository.findByOrderStatusIs("admin", data);
			model.addAttribute("orders", d);
			model.addAttribute("totalPage", d.getTotalPages());
			model.addAttribute("next", "placed,");
			
		} else if (action.trim().equals("notPlaced")) {
			Page<BookOrder> d = bookOrderRepository.findByOrderStatusIsOrOrderStatusIs("store", "admin-store", data);
			model.addAttribute("orders", d);
			model.addAttribute("totalPage", d.getTotalPages());
			model.addAttribute("next", "notPlaced,");
			/*
			 * for (BookOrder bo : orders) { if (bo.getOrderStatus().equals("store") ||
			 * bo.getOrderStatus().equals("admin-store")) filterData.add(bo); }
			 */
		} else if (action.trim().equals("cancel")) {
			Page<BookOrder> d = bookOrderRepository.findByOrderStatusIs("cancel", data);
			System.out.println(d.toString() + "sizee : " + d.getSize());

			model.addAttribute("orders", d);
			model.addAttribute("totalPage", d.getTotalPages());
			model.addAttribute("next", "cancel,");

			/*
			 * for (BookOrder bo : orders) { if (bo.getOrderStatus().equals("cancel"))
			 * 
			 * filterData.add(bo); }
			 */
		}
		model.addAttribute("alert", "hide");
		model.addAttribute("repo", bookStoreRepository);
		model.addAttribute("currentPage", page);
		model.addAttribute("showPagination", true);
		return "/admin/OrderCheckout";
	}

	@RequestMapping("/searchOrder/{id}")
	public String SearchOrder(@PathVariable("id") int id, Model model) throws Exception {
		BookOrder bod = bookOrderRepository.findByOrderId(id);

		System.out.println(" Search order : " + id);
		List<BookOrder> orderList = new ArrayList<>();
		if (bod != null) {
			orderList.add(bod);
			System.out.println("order present ");
			model.addAttribute("orders", orderList);
			model.addAttribute("totalPage", 1);
			model.addAttribute("next", "all,");
			model.addAttribute("alert", "hide");
			model.addAttribute("showPagination", false);
		} else {
			System.out.println("Order not present");
			model.addAttribute("alert", "show");
			Pageable pageable = PageRequest.of(0, 8);
			Page<BookOrder> d = bookOrderRepository.findAll(pageable);
			model.addAttribute("orders", d);
			model.addAttribute("totalPage", d.getTotalPages());
			model.addAttribute("next", "all,");
			model.addAttribute("showPagination", true);

		}
		model.addAttribute("currentPage", 0);
		model.addAttribute("repo", bookStoreRepository);

		return "/admin/OrderCheckout";
	}

	@RequestMapping("/do_placeTheOrderPayment")
	@ResponseBody
	public String placeTheOrderPaymentAdminToOwner(@RequestBody Map<String, Object> map) throws Exception {
		String rzKey = map.get("key").toString();
		int id = Integer.parseInt(map.get("id").toString().trim());
		String rzSecret = map.get("secret").toString();
		int price = Integer.parseInt(map.get("price").toString().trim());
		if (rzKey.equalsIgnoreCase("null") || rzSecret.equalsIgnoreCase("null") || rzKey.equalsIgnoreCase("")
				|| rzSecret.equalsIgnoreCase("")) {
			BookOrder bookOrder = bookOrderRepository.findByOrderId(id);
			bookOrder.setOrderStatus("admin-store");
			bookOrderRepository.save(bookOrder);
			JSONObject options = new JSONObject();
			options.put("status", "offline");
			options.put("offlinePayment", bookOrder.getOrderedBooks().getSeller().getBookStore().getPlaceOrderPrice());
			return options.toString();
		}

		RazorpayClient client = new RazorpayClient(rzKey, rzSecret);
		JSONObject options = new JSONObject();
		options.put("amount", price * 100);
		options.put("currency", "INR");
		options.put("receipt", "txn_101010");
		Order order = client.Orders.create(options);

		BookOrder o = bookOrderRepository.findByOrderId(id);
		BookStore bookStore = o.getOrderedBooks().getSeller().getBookStore();
		bookStore.setPlaceOrderPrice(bookStore.getPlaceOrderPrice() - price);
		bookStore.setTotalEarnings(bookStore.getTotalEarnings() + price);
		bookStoreRepository.save(bookStore);
		return order.toString();
	}

	@RequestMapping("/saveThePlaceOrderStatus")
	@ResponseBody
	public String changeTheStatusPlaceToStore(@RequestBody Map<String, Object> map) {
		int id = Integer.parseInt(map.get("id").toString().trim());
		BookOrder bookOrder = bookOrderRepository.findByOrderId(id);
		bookOrder.setOrderStatus("admin-store");
		bookOrderRepository.save(bookOrder);
		JSONObject options = new JSONObject();
		options.put("status", "success");
		return options.toString();
	}

	@RequestMapping("/stores/{action}")
	public String storeDetails(Model model, @PathVariable("action") String actionUrl) {
		String a[] = actionUrl.trim().split(",");
		String action = a[0].toString();
		int page = Integer.parseInt(a[1].toString());
		if (action.equals("all")) {
			System.out.println("all");
			Pageable pageable = PageRequest.of(page, 8);
			Page<BookStore> list = bookStoreRepository.findAll(pageable);
			model.addAttribute("stores", list);
			model.addAttribute("action", "all");
			model.addAttribute("totalPage", list.getTotalPages());
		} else if (action.equals("check")) {
			System.out.println("check");
			Pageable pageable = PageRequest.of(page, 8);
			Page<BookStore> list = bookStoreRepository.findByCheckByAdminTrue(pageable);
			model.addAttribute("stores", list);
			model.addAttribute("action", "check");
			model.addAttribute("totalPage", list.getTotalPages());
		} else if (action.equals("uncheck")) {
			System.out.println("uncheck");
			Pageable pageable = PageRequest.of(page, 32);
			Page<BookStore> list = bookStoreRepository.findByCheckByAdminFalse(pageable);
			model.addAttribute("stores", list);
			model.addAttribute("action", "uncheck");
			model.addAttribute("totalPage", list.getTotalPages());
		}
		model.addAttribute("showPagination", true);
		model.addAttribute("currentPage", page);
		model.addAttribute("alert", "hide");
		return "/admin/StoreCheck";
	}

	@RequestMapping("/searchStore/{id}")
	public String SearchStore(@PathVariable("id") int id, Model model) throws Exception {
		BookStore bst = bookStoreRepository.findByStoreId(id);

		System.out.println(" Search store : " + id);
		List<BookStore> storeList = new ArrayList<>();
		if (bst != null) {
			storeList.add(bst);
			System.out.println("store present " + bst.getStoreName());
			model.addAttribute("stores", storeList);
			model.addAttribute("action", "all");
			model.addAttribute("totalPage", 1);
			model.addAttribute("currentPage", 0);
			model.addAttribute("alert", "hide");
			model.addAttribute("showPagination", false);
		} else {
			System.out.println("store not present");
			model.addAttribute("alert", "show");
			Pageable pageable = PageRequest.of(0, 8);
			Page<BookStore> list = bookStoreRepository.findAll(pageable);
			model.addAttribute("stores", list);
			model.addAttribute("action", "all");
			model.addAttribute("totalPage", list.getTotalPages());
			model.addAttribute("showPagination", true);
			model.addAttribute("currentPage", 0);
		}

		return "/admin/StoreCheck";
	}

	/*
	 * @RequestMapping("/stores/{id}") public String
	 * storeCheckSearch(@PathVariable("id") int id, Model model) {
	 * 
	 * try { BookStore bst = bookStoreRepository.getById(id);
	 * 
	 * List<BookStore> storeList = new ArrayList<>(); if (bst != null)
	 * storeList.add(bst); model.addAttribute("stores", storeList);
	 * model.addAttribute("action", "uncheck"); model.addAttribute("totalPage", 1);
	 * model.addAttribute("currentPage", 0); } catch (Exception e) {
	 * 
	 * e.printStackTrace(); } return "/admin/StoreCheck"; }
	 */

	@RequestMapping("/books/{action}")
	public String allBooksDetails(Model model, @PathVariable("action") String actionUrl) {
		String a[] = actionUrl.trim().split(",");
		String action = a[0].toString();
		int page = Integer.parseInt(a[1].toString());
		Pageable pageable = PageRequest.of(page, 8);
		Page<Book> list = null;
		if (action.equals("all")) {

			list = bookRepository.findAll(pageable);
			model.addAttribute("books", list);
			model.addAttribute("currentPage", page);
			model.addAttribute("action", "all");
			model.addAttribute("totalPage", list.getTotalPages());
		} else if (action.equals("store")) {
			System.out.println("store");

			list = bookRepository.findByStoreIdIsNotAndBookQuantityGreaterThanAndSaleTrue(0, 0, pageable);
			
			model.addAttribute("action", "all");
			// model.addAttribute("totalPage", list.getTotalPages());

		} else if (action.equals("user")) {
			System.out.println("user");

			list = bookRepository.findByStoreIdIsAndBookQuantityGreaterThanAndSaleTrue(0, 0, pageable);
			
			model.addAttribute("action", "user");
			/* model.addAttribute("totalPage", list.getTotalPages()); */
		} else if (action.equals("check")) {
			System.out.println("check");

			list = bookRepository.findByStoreIdIsNotAndBookQuantityGreaterThanAndIsBestOfYearTrueAndSaleTrue(0, 0,
					pageable);
			
			model.addAttribute("action", "check");
			/* model.addAttribute("totalPage", list.getTotalPages()); */

		} else if (action.equals("uncheck")) {
			System.out.println("uncheck");

			list = bookRepository.findByStoreIdIsNotAndBookQuantityGreaterThanAndIsBestOfYearFalseAndSaleTrue(0, 0,
					pageable);
			

			model.addAttribute("action", "uncheck");

		}
		model.addAttribute("books", list);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", list.getTotalPages());
		model.addAttribute("alert", "hide");
		model.addAttribute("showPagination", true);

		return "/admin/books";

	}

	@RequestMapping("/searchBook/{page}/{name}")
	public String SearchBookById(@PathVariable int page, @PathVariable("name") String name, Model model)
			throws Exception {

		Pageable pageable = PageRequest.of(page, 8);
		Page<Book> books = bookRepository.findByBookQuantityGreaterThanAndBookTitleContainingAndSaleTrue(0, name,
				pageable);
		
		model.addAttribute("books", books);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", books.getTotalPages());
		model.addAttribute("alert", "hide");
		model.addAttribute("showPagination", true);
		return "/admin/books";
	}

	@RequestMapping("/users/{page}")
	public String allUsersDetails(Model model, @PathVariable("page") int page) {
		Pageable pageable = PageRequest.of(page, 8);
		Page<User> list = userRepository.findAll(pageable);
		model.addAttribute("users", list);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", list.getTotalPages());
		return "/admin/users";
	}

	@RequestMapping("/storeCheckAction")
	@ResponseBody
	public String storeCheckAction(@RequestBody Map<String, Object> map) {
		System.out.println("I am here");
		BookStore bookStore = bookStoreRepository.findByStoreId(Integer.parseInt(map.get("sid").toString()));

		bookStore.setCheckByAdmin(!bookStore.isCheckByAdmin());
		bookStoreRepository.save(bookStore);
		for (Book b : bookStore.getOwner().getBooks()) {
			if (bookStore.isCheckByAdmin())
				b.setStoreId(bookStore.getStoreId());
			else
				b.setStoreId(0);
			bookRepository.save(b);
		}
		System.out.println("I am here");
		JSONObject options = new JSONObject();
		boolean flag = EmailActions.sendActiveStoreMessage(bookStore.getOwner(), javaMailSendar);
		System.out.println("Activate account message : " + flag);
		options.put("status", "success");
		return options.toString();

	}

	@RequestMapping("/BestBookOfYearAction")
	@ResponseBody
	public String AdminTakeTheActionOfBestBooks(@RequestBody Map<String, Object> map) {
		System.out.println("I am here");
		Book book = bookRepository.getById(Integer.parseInt(map.get("bid").toString()));
		book.setBestOfYear(!book.isBestOfYear());
		List<BookRating> r = new ArrayList<>();
		bookRepository.save(book);
		if (book.getBookRating().isEmpty()) {
			BookRating brating = new BookRating();
			brating.setBook(book);
			brating.setBookRate(5);
			brating.setDate(LocalDate.now());
			brating.setRateuser(user);
			brating.setReview("This is best book of year..");
			r.add(brating);
			book.setBookRating(r);
			bookRepository.save(book);
		}

		JSONObject options = new JSONObject();
		options.put("status", "success");
		return options.toString();
	}

	@RequestMapping("/SingleuserDetailes/{action}")
	public String SingleuserDetailes(Model model, @PathVariable("action") String actionUrl) {
		String a[] = actionUrl.trim().split(",");
		int id = Integer.parseInt(a[0].toString());
		int page = Integer.parseInt(a[1].toString());
		User u = userRepository.getById(id);
		model.addAttribute("u", u);
		model.addAttribute("page", page);
		model.addAttribute("title", "user details");
		return "/admin/singleUserPage";
	}

	@RequestMapping("/myProfile")
	public String userProile() {
		return "/admin/profile";
	}

	@RequestMapping("/editProfilePage")
	public String editProfilepage() {
		return "/admin/editprofile";
	}

	@PostMapping("/do_edit")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
			@RequestParam("userpicture") MultipartFile file, HttpSession session) {

		try {
			if (result.hasErrors()) {
				model.addAttribute("user", user);

				return "admin/editprofile";
			}
			if (!file.isEmpty()) {
				if (!user.getUserPic().equals("default.png")) {
					boolean f = ImageDelete.delete("static/image/webContent/userImages", user.getUserPic());
				}
				String fname = "User" + new Random(1000).nextInt() + file.getOriginalFilename();
				boolean flag = ImageSaver.save(file, "static/image/webContent/userImages", fname);
				if (flag == true)
					System.out.println("New Image will save");
				user.setUserPic(fname+"img");
			}

			// user.setUserPassword(encPassword.encode(user.getUserPassword()));
			System.out.println(user);
			userRepository.save(user);
			// System.out.println(user.getUserId() + " is saved !!");
			return "/admin/editprofile";

		} catch (Exception e) {
			e.printStackTrace();

			// TODO: handle exception
		}
		return "/admin/editprofile";
	}// end registration method

	@RequestMapping("/storeInformation/{action}")
	public String displayStore(@PathVariable("action") String str, Model model) {
		System.out.println(str);
		BookStore bookStore = bookStoreRepository.findByStoreId(Integer.parseInt(str.split(",")[0]));
		model.addAttribute("page", Integer.parseInt(str.split(",")[1]));
		model.addAttribute("action", str.split(",")[2]);
		return "/admin/storeInformation";
	}

	@RequestMapping("/feedback/{page}")
	public String feedback(@PathVariable("page") int page, Model model) {
		Pageable pageable = PageRequest.of(page, 5, Sort.by("date").ascending());
		Page<UserFeedback> fpage = feedbackRepository.findAll(pageable);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPage", fpage.getTotalPages());
		model.addAttribute("userfeedbacks", fpage);
		return "/admin/feedback";
	}

}
