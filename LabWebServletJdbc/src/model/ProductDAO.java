package model;

import java.util.List;

public interface ProductDAO {

	public abstract ProductBean select(int id);

	public abstract List<ProductBean> select();

	public abstract ProductBean insert(ProductBean bean);

	public abstract ProductBean update(String name, double price,
			java.util.Date make, int expire, int id);

	public abstract boolean delete(int id);

}