package sk.annotation.library.jam.example.ex17;

import lombok.Data;

@Data
public class Obj1 {
	private String val;

	public Obj1(String val){
		this.val = val;
	}

	public Obj1(){
	}
}
