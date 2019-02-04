package examples;

import examples.data.I1;
import examples.data.I2;
import examples.data.IC;
import examples.data.OC;
import sk.annotation.library.mapper.fast.annotations.FastMapper;
import sk.annotation.library.mapper.fast.annotations.Return;

//@FastMapper
public interface MapSimple {
//	public O1 methodSimple1(I1 i1);

	public OC methodMulti(I1 i1, I2 i2, IC i3);

	public OC methodMultiWithRet(I1 i1, I2 i2, IC i3, @Return OC oc);

//	default public O2 methodSimple2_implemented(I2 i2) {
//		if (i2 == null) return null;
//		O2 o1 = new O2();
//		o1.setId(i2.getId());
//		o1.setValue2(i2.getValue2());
//		return o1;
//	}
//
//	public OC methodSimpleC(I1 i1);
}
