package sk.annotation.library.mapper.jam.annotation;

public class DataType1a {
	public Long id;
	public Long parentId;

	private String name;

	private DataType1a parent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType1a getParent() {
		return parent;
	}

	public void setParent(DataType1a parent) {
		this.parent = parent;
	}
}
