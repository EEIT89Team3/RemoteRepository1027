package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import model.ProductBean;
import model.ProductDAO;

public class ProductDAOJdbc implements ProductDAO {
	private DataSource dataSource;
	public ProductDAOJdbc() {
		try {
			Context ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/xxx");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	private static final String SELECT_BY_ID =
			"select * from product where id=?";
	@Override
	public ProductBean select(int id) {
		ProductBean result = null;
		ResultSet rset = null;
		try(//Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			Connection conn = dataSource.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID);) {
			
			stmt.setInt(1, id);
			rset = stmt.executeQuery();
			if(rset.next()) {
				result = new ProductBean();
				result.setId(rset.getInt("id"));
				result.setName(rset.getString("name"));
				result.setPrice(rset.getDouble("price"));
				result.setMake(rset.getDate("make"));
				result.setExpire(rset.getInt("expire"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rset!=null) {
				try {
					rset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	private static final String SELECT_ALL =
			"select * from product";
	@Override
	public List<ProductBean> select() {
		List<ProductBean> result = null;
		try(//Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			Connection conn = dataSource.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
			ResultSet rset = stmt.executeQuery();) {
			
			result = new ArrayList<ProductBean>();
			while(rset.next()) {
				ProductBean bean = new ProductBean();
				bean.setId(rset.getInt("id"));
				bean.setName(rset.getString("name"));
				bean.setPrice(rset.getDouble("price"));
				bean.setMake(rset.getDate("make"));
				bean.setExpire(rset.getInt("expire"));
				
				result.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static final String INSERT =
			"insert into product (id, name, price, make, expire) values (?, ?, ?, ?, ?)";
	@Override
	public ProductBean insert(ProductBean bean) {
		ProductBean result = null;
		try(//Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			Connection conn = dataSource.getConnection();
			PreparedStatement stmt = conn.prepareStatement(INSERT);) {
			if(bean!=null) {
				stmt.setInt(1, bean.getId());
				stmt.setString(2, bean.getName());
				stmt.setDouble(3, bean.getPrice());
				java.util.Date make = bean.getMake();
				if(make!=null) {
					long time = make.getTime();
					stmt.setDate(4, new java.sql.Date(time));
				} else {
					stmt.setDate(4, null);				
				}
				stmt.setInt(5, bean.getExpire());
				int i = stmt.executeUpdate();
				if(i==1) {
					result = bean;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static final String UPDATE =
			"update product set name=?, price=?, make=?, expire=? where id=?";
	@Override
	public ProductBean update(String name,
			double price, java.util.Date make, int expire, int id) {
		ProductBean result = null;
		try(//Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			Connection conn = dataSource.getConnection();
			PreparedStatement stmt = conn.prepareStatement(UPDATE);) {
			stmt.setString(1, name);
			stmt.setDouble(2, price);
			if(make!=null) {
				long time = make.getTime();
				stmt.setDate(3, new java.sql.Date(time));
			} else {
				stmt.setDate(3, null);				
			}
			stmt.setInt(4, expire);
			stmt.setInt(5, id);
			int i = stmt.executeUpdate();
			if(i==1) {
				result = this.select(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static final String DELETE =
			"delete from product where id=?";
	@Override
	public boolean delete(int id) {
		try(//Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			Connection conn = dataSource.getConnection();
			PreparedStatement stmt = conn.prepareStatement(DELETE);) {			
			stmt.setInt(1, id);
			int i = stmt.executeUpdate();
			if(i==1) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
