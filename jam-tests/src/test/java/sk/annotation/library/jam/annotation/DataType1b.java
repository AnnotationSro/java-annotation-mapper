package sk.annotation.library.jam.annotation;

public class DataType1b {
	private Long id;

	private String name;

	private DataType2 parent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType2 getParent() {
		return parent;
	}

	public void setParent(DataType2 parent) {
		this.parent = parent;
	}
}
