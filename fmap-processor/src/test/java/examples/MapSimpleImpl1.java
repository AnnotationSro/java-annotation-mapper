package examples;

import examples.data.*;
import sk.annotation.library.mapper.fast.annotations.Return;

import static sk.annotation.library.mapper.fast.utils.context.MapperUtil.doTransform;

abstract public class MapSimpleImpl1 {//implements MapSimple {

//	@Override
//	public O1 methodSimple1(I1 i1) {
//		return methodSimple1_withRet(i1, null);
//	}
//	// Created from: methodSimple1
//	public O1 methodSimple1_withRet(I1 i1, @Return O1 o1) {
//		return doTransform(new Object[] {i1}, o1, O1::new, (retO1) -> {
//			retO1.setId(i1.getId());
//		});
//	}
//
//
//	@Override
//	public OC methodMulti(I1 i1, I2 i2, IC i3) {
//		return methodMultiWithRet(i1, i2, i3, null);
//	}
//
//	@Override
//	public OC methodMultiWithRet(I1 i1, I2 i2, IC i3, @Return OC o1) {
//		return doTransform(new Object[] {i1, i2, i3}, o1, OC::new, (retOC) -> {
//			methodSimpleC_withRet(i1, retOC);
//			methodSimpleC_withRet(i2, retOC);
//			methodSimpleC_withRet(i3, retOC);
//		});
//	}
//
//
//	@Override
//	public OC methodSimpleC(I1 i1) {
//		return methodSimpleC_withRet(i1, null);
//	}
//
//	// public OC methodSimpleC(I1 i1)
//	// public OC methodMultiWithRet(I1 i1, I2 i2, IC i3, @Return OC o1)
//	public OC methodSimpleC_withRet(I1 i1, @Return OC oc) {
//		return doTransform(new Object[] {i1}, oc, OC::new, (retOC) -> {
//			retOC.setId(i1.getId());
//		});
//	}
//
//	// public OC methodMultiWithRet(I1 i1, I2 i2, IC i3, @Return OC o1)
//	public OC methodSimpleC_withRet(I2 i2, @Return OC oc) {
//		return doTransform(new Object[] {i2}, oc, OC::new, (retOC) -> {
//			retOC.setId(i2.getId());
//		});
//	}
//
//	public OC methodSimpleC_withRet(IC i3, @Return OC oc) {
//		return doTransform(new Object[] {i3}, oc, OC::new, (retOC) -> {
//			retOC.setId(i3.getId());
//			retOC.setValue1(i3.getValue1());
//			retOC.setValue2(i3.getValue2());
//			retOC.setSubObj1(methodSimple1_withRet(i3.getSubObj1(), retOC.getSubObj1()));
//			retOC.setSubObj2(methodSimple2_implemented_bypass(i3.getSubObj2(), retOC.getSubObj2()));
//		});
//	}
//
//
//	// Created from: methodSimpleC_withRet
//	public O2 methodSimple2_implemented_bypass(I2 i2, @Return O2 o2) {
//		// if extended mapper has defined method I2 -> O2, we have to use it following:
//		return doTransform(new Object[] {i2}, o2, () -> methodSimple2_implemented(i2), null);
//	}

}
