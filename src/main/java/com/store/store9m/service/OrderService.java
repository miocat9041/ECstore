package com.store.store9m.service;


import java.util.List;

import com.store.store9m.model.OrderRequest;
import com.store.store9m.model.ProductOrder;


public interface OrderService {

	public void saveOrder(int userid,OrderRequest orderRequest);
	
	public List<ProductOrder> getOrdersByUser(Integer userId);

	public Boolean updateOrderStatus(Integer id, String status);
	
	public List<ProductOrder> getAllOrders();
	
	public ProductOrder getOrdersByOrderId(String orderId);
	
}
