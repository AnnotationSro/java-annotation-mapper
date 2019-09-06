package sk.annotation.library.jam.example.ex15;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AbstractData {
	private String a = null;
	private String b = null;
	private String c = null;
	private String d = null;
	private String e = null;
	private String f = null;
	private String g = null;
	private String h = null;


	static protected int i = 1;

	static public <T extends AbstractData> T random(T d) {
		d.setA("val" + i++);
		d.setB("val" + i++);
		d.setC("val" + i++);
		d.setD("val" + i++);
		d.setE("val" + i++);
		d.setF("val" + i++);
		d.setG("val" + i++);
		d.setH("val" + i++);
		return d;
	}

	static public Data1 createData1() {
		return random(new Data1());
	}

	static public Data2 createData2() {
		Data2 d2 = random(new Data2());
		d2.setD1(createData1());
		return d2;
	}

	static public Data3 createData3() {
		Data3 d3 = random(new Data3());
		d3.setD1(createData1());
		d3.setD2(createData2());
		return d3;
	}

}
