package com.store.store9m.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.store.store9m.model.Category;
import com.store.store9m.model.Product;
import com.store.store9m.model.ProductOrder;
import com.store.store9m.model.UserDtls;
import com.store.store9m.service.CartService;
import com.store.store9m.service.CategoryService;
import com.store.store9m.service.OrderService;
import com.store.store9m.service.ProductService;
import com.store.store9m.service.UserService;
import com.store.store9m.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private  UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}
	
	@GetMapping("/")
	public String index() 
	{
		
		return "admin/index";
	}
	
	@GetMapping("/loadAddProduct")
	public String loadAddProduct(Model m) 
	{
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories",categories);
		return "admin/addproduct";
	}
	
	@GetMapping("/category")
	public String category(Model m) 
	{
		m.addAttribute("categorys",categoryService.getAllCategory());
		
		return "admin/category";
	}
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException{
		
		String imageName =file!=null ?file.getOriginalFilename(): "default.jpg";
		category.setImageName(imageName);
		
		Boolean existCategory = categoryService.existCategory(category.getName());
		if(existCategory)
		{
			session.setAttribute("errorMsg", "此類別已建立過");
		}else {
			Category saveCategory = categoryService.saveCategory(category);
			
			if(ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg","未儲存，伺服器連線有問題");
			}else {
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category"+File.separator
						+ file.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				session.setAttribute("succMsg","儲存成功");
			}
		}

		return "redirect:/admin/category";
	}
	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id,HttpSession session) 
	{
		Boolean deleteCategory = categoryService.deleteCategory(id);
		
		if(deleteCategory)
		{
			session.setAttribute("succMsg","類別刪除成功");
			
		}else {
			session.setAttribute("errorMsg","發生問題無法刪除成功");
		}
		
		return "redirect:/admin/category";
	}
	
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m)
	{
		m.addAttribute("category",categoryService.getCategoryById(id));
		return "admin/editcategory";
	}
	
	@PostMapping("/updateCategory")
	public String updateCategory (@ModelAttribute Category category,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
		
		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName=file.isEmpty() ? oldCategory.getImageName():file.getOriginalFilename();
	
	if(!ObjectUtils.isEmpty(category)) {
		
		oldCategory.setName(category.getName());
		oldCategory.setIsActive(category.getIsActive());
		oldCategory.setImageName(imageName);
	}
	
	Category updateCategory = categoryService.saveCategory(oldCategory);
	
	if(!ObjectUtils.isEmpty(updateCategory))
	{
		if(!file.isEmpty())
		{
			File saveFile = new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category"+File.separator
					+ file.getOriginalFilename());
			
			//System.out.println(path);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);		
			
		}
		
		session.setAttribute("succMsg","類別更新成功");
	}else {
		session.setAttribute("errorMsg","發生問題無法刪除成功");
	}
	
	return "redirect:/admin/loadEditCategory/"+category.getId();
	}
	
	@PostMapping("/saveProduct")
	public String saveProduct (@ModelAttribute Product product,@RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException
	{
		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		
		Product saveProduct = productService.saveProduct(product);
		
		if(!ObjectUtils.isEmpty(saveProduct)) {
			File saveFile = new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"product"+File.separator
					+ image.getOriginalFilename());
			
			// System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		
			session.setAttribute("succMsg", "新產品建立成功");
		}else {
			session.setAttribute("errorMsg", "發生問題，無法建立");
		}
		
		
		return "redirect:/admin/loadAddProduct";
	}
	
	@GetMapping("/products")
	public String loadViewProduct(Model m, @RequestParam(defaultValue = "") String ch) {
		List<Product> products = null;
		if (ch != null && ch.length() > 0) {
			products = productService.searchProduct(ch);
		} else {
			products = productService.getAllProducts();
		}
		m.addAttribute("products", products);
		return "admin/products";
	}
	
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "成功刪除產品");
		} else {
			session.setAttribute("errorMsg", "發生錯誤，無法刪除產品");
		}
		return "redirect:/admin/products";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategory());
		return "admin/editproduct";
	}
	
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "無效優惠折數，不可輸入");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "產品更新成功");
			} else {
				session.setAttribute("errorMsg", "發生問題，無法更新");
			}
		}
		return "redirect:/admin/editProduct/" + product.getId();
	}
	
	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDtls> users = userService.getUsers("ROLE_USER");
		m.addAttribute("users", users);
		return "/admin/users";
	}
	
	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			session.setAttribute("succMsg", "帳號已成功更新");
		} else {
			session.setAttribute("errorMsg", "發生錯誤，無法更新");
		}
		return "redirect:/admin/users";
	}
	
	@GetMapping("/orders")
	public String getAllOrders(Model m) {
		List<ProductOrder> allOrders = orderService.getAllOrders();
		m.addAttribute("orders", allOrders);
		return "/admin/orders";
	}

	@PostMapping("/update-order-status")
	public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		Boolean updateOrder = orderService.updateOrderStatus(id, status);

		if (updateOrder) {
			session.setAttribute("succMsg", "狀態已更新");
		} else {
			session.setAttribute("errorMsg", "狀態未更新，請重試");
		}
		return "redirect:/admin/orders";
	}
	
	@GetMapping("/search-order")
	public String searchProduct(@RequestParam String orderId, Model m, HttpSession session) {

		if (orderId != null && orderId.length() > 0) {

			ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());

			if (ObjectUtils.isEmpty(order)) {
				session.setAttribute("errorMsg", "orderId不正確");
				m.addAttribute("orderDtls", null);
			} else {
				m.addAttribute("orderDtls", order);
			}

			m.addAttribute("srch", true);
		} else {
			List<ProductOrder> allOrders = orderService.getAllOrders();
			m.addAttribute("orders", allOrders);
			m.addAttribute("srch", false);
		}
		return "/admin/orders";

	}

}
