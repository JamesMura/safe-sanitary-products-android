package hack.sanitation.safe_sanitary_products.entity;

import com.google.gson.annotations.Expose;

public class Product {
	@Expose
	private String name;
	@Expose
	private String manufactuers_name;
	@Expose
	private String expiryDate;
	@Expose
	private String manufactureDate;
	@Expose
	private String photo_url;
	@Expose
	private String description;
	@Expose
	private String product_code;
	@Expose
	private String product_id;
	@Expose
	private int status;

	public String getName() {
		return name;
	}

	public String getManufactuers_name() {
		return manufactuers_name;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public String getManufactureDate() {
		return manufactureDate;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public String getDescription() {
		return description;
	}

	public String getProduct_code() {
		return product_code;
	}

	public String getProduct_id() {
		return product_id;
	}

	public int getStatus() {
		return status;
	}

	public boolean isFake() {
		return this.status != 1;
	}

}
