package hack.sanitation.safe_sanitary_products.entity;

public class Product {
	private String name;
	private String manufactuers_name;
	private String expiryDate;
	private String manufactureDate;
	private String photo_url;
	private String description;
	private String product_code;
	private String product_id;
	private int status;

	public boolean isFake() {
		return this.status != 1;
	}

}
