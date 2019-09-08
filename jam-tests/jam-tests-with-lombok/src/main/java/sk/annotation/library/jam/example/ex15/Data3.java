package sk.annotation.library.jam.example.ex15;

import lombok.Getter;
import lombok.Setter;
import sk.annotation.library.jam.example.ex15.subpackage.Data1;

@Getter
@Setter
public class Data3 extends AbstractData {
	private Data1 d1;
	private Data2 d2;
}
