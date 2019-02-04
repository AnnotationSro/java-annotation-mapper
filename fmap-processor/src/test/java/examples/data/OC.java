package examples.data;

public class OC {
	private String id;
	private String value1;
	private String value2;
	private O1 subObj1;
	private O2 subObj2;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public O1 getSubObj1() {
		return subObj1;
	}

	public void setSubObj1(O1 subObj1) {
		this.subObj1 = subObj1;
	}

	public O2 getSubObj2() {
		return subObj2;
	}

	public void setSubObj2(O2 subObj2) {
		this.subObj2 = subObj2;
	}
}
