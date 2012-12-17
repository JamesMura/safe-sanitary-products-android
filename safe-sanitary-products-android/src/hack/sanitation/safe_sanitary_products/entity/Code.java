package hack.sanitation.safe_sanitary_products.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;

public class Code {
	@Expose
	@SerializedName("actual_code")
	private String actualCode;
	@Expose
	private float latitude;
	@Expose
	private float longitude;
	
	@Expose
	private int status;

	public String getActualCode() {
		return actualCode;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public Code(String code,LocationInfo location) {
		actualCode = code;
		latitude=location.lastLat;
		longitude= location.lastLong;
	}

}
