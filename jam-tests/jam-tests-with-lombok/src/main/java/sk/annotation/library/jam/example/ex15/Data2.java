package sk.annotation.library.jam.example.ex15;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Data2 extends AbstractData implements IData {
	private Data1 d1;
}
