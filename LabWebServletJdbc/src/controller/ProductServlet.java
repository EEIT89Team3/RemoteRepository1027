package controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ProductBean;
import model.ProductService;

@WebServlet(
		urlPatterns={"/pages/product.controller"}		
)
public class ProductServlet extends HttpServlet {
	private SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ProductService service;
	@Override
	public void init() throws ServletException {
		service = new ProductService();
	}
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//接收資料
		String temp1 = request.getParameter("id");
		String name = request.getParameter("name");
		String temp2 = request.getParameter("price");
		String temp3 = request.getParameter("make");
		String temp4 = request.getParameter("expire");
		String prodaction = request.getParameter("prodaction");
		
//驗證資料
		Map<String, String> errors = new HashMap<String, String>();
		request.setAttribute("error", errors);
		
		if(prodaction!=null) {
			if(prodaction.equals("Insert") || prodaction.equals("Update") || prodaction.equals("Delete")) {
				if(temp1==null || temp1.length()==0) {
					errors.put("id", "Please enter Id for "+prodaction);
				}
			}
		}
		
//轉換資料
		int id = 0;
		if(temp1!=null && temp1.length()!=0) {
			try {
				id = Integer.parseInt(temp1);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				errors.put("id", "Id must be an integer");
			}
		}
		double price = 0;
		if(temp2!=null && temp2.length()!=0) {
			try {
				price = Double.parseDouble(temp2);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				errors.put("price", "Price must be a number");				
			}
		}
		java.util.Date make = null;
		if(temp3!=null && temp3.length()!=0) {
			try {
				make = sFormat.parse(temp3);
			} catch (ParseException e) {
				e.printStackTrace();
				errors.put("make", "Make must be a date of YYYY-MM-DD");
			}
		}
		int expire = 0;
		if(temp4!=null && temp4.length()!=0) {
			try {
				expire = Integer.parseInt(temp4);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				errors.put("expire", "Expire must be an integer");
			}
		}
		
		if(errors!=null && !errors.isEmpty()) {
			request.getRequestDispatcher(
					"/pages/product.jsp").forward(request, response);
			return;
		}

//呼叫Model
		ProductBean bean = new ProductBean();
		bean.setId(id);
		bean.setName(name);
		bean.setPrice(price);
		bean.setMake(make);
		bean.setExpire(expire);
		
//根據Model執行結果導向View
		if(prodaction!=null && prodaction.equals("Select")) {
			List<ProductBean> result = service.select(bean);
			request.setAttribute("select", result);
			request.getRequestDispatcher(
					"/pages/display.jsp").forward(request, response);
		} else if(prodaction!=null && prodaction.equals("Insert")) {
			ProductBean result = service.insert(bean);
			if(result==null) {
				errors.put("action", "Insert fail");
			} else {
				request.setAttribute("insert", result);
			}
			request.getRequestDispatcher(
					"/pages/product.jsp").forward(request, response);
		} else if(prodaction!=null && prodaction.equals("Update")) {
			ProductBean result = service.update(bean);
			if(result==null) {
				errors.put("action", "Update fail");
			} else {
				request.setAttribute("update", result);
			}
			request.getRequestDispatcher(
					"/pages/product.jsp").forward(request, response);
		} else if(prodaction!=null && prodaction.equals("Delete")) {
			boolean result = service.delete(bean);
			if(!result) {
				request.setAttribute("delete", 0);
			} else {
				request.setAttribute("delete", 1);
			}
			request.getRequestDispatcher(
					"/pages/product.jsp").forward(request, response);
		} else  {
			errors.put("action", "Unknown Action:"+prodaction);
			request.getRequestDispatcher(
					"/pages/product.jsp").forward(request, response);
		}

		
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doGet(req, resp);
	}
}
