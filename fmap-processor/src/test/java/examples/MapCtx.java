package examples;

import examples.data.*;
import sk.annotation.library.mapper.fast.annotations.Context;
import sk.annotation.library.mapper.fast.annotations.Return;

public interface MapCtx {
	// default implementation required context
	default public O1 methodSimpleO1_implemented(I1 i1, @Context Object ctx) {
		if (i1 == null) return null;

		O1 o1 = new O1();
		o1.setId(i1.getId());
		o1.setValue1(i1.getValue1() + ctx.hashCode());

		return o1;
	}

	// default implementation without context
	default public O2 methodSimpleO2(I2 i2/*, @Context Object ctx*/) {
		if (i2 == null) return null;
		O2 o2 = new O2();
		o2.setId(i2.getId());
		o2.setValue2(i2.getValue2());

		// ak by nahodou bolo treba volat methodSimpleO1 - nebude sa to dat

		return o2;
	}

	public OC methodCOntext_IC_OC(IC ic, @Context Object ctx, @Context("newValue") Object ctx2);
	public O1 methodSimpleO1(IC i1, @Return O1 o1, @Context Object ctx, @Context("newValue") Object ctx2);
}
