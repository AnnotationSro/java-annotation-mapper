package examples;

import examples.data.*;
import sk.annotation.library.mapper.fast.annotations.Context;
import sk.annotation.library.mapper.fast.annotations.Return;
import sk.annotation.library.mapper.fast.utils.context.MapperUtil;

import static sk.annotation.library.mapper.fast.utils.context.MapperUtil.doTransform;

public class MapCtxImpl implements MapCtx {

	@Override
	public OC methodCOntext_IC_OC(IC ic, /*@Context */Object ctx, /*@Context("newValue") */Object ctx2) {
		return doTransform(
				null,
				// Init context
				() -> {
					MapperUtil.putContextValue("ctx", ctx);
					MapperUtil.putContextValue("newValue", ctx2);
				},
				() -> methodSimpleOC_noContext(ic)
		);
	}

	public OC methodSimpleOC_noContext(IC ic) {
		return methodSimpleOC(ic, null);
	}
	public OC methodSimpleOC(IC ic, @Return OC oc) {
		return null;
	}

	@Override
	public O1 methodSimpleO1(IC i1, @Return O1 o1, @Context Object ctx, @Context("newValue") Object ctx2) {
		// Transform to API without CTX
		return doTransform(
				null,
				// Init context
				() -> {
					MapperUtil.putContextValue("ctx", ctx);
					MapperUtil.putContextValue("newValue", ctx2);
				},
				() -> methodSimpleO1_implemented_bypass(i1, o1)
		);
	}
	public O1 methodSimpleO1_implemented_bypass(IC i1, @Return O1 o1) {
		return null;
		/*// if extended mapper has defined method I2 -> O2 (ctx not important), we have to use it following:

		// Redirected to: default public O1 methodSimpleO1_implemented(I1 i1, @Context Object ctx)
		return goFromCache(new Object[] {i1}, o1, () -> methodSimpleO1_implemented(i1, InstanceCacheUtil.ContextData.getValue("ctx")), null);
		return goFromCache(
				new Object[] {i1},
				O1::new,
				o1,
				(ctx) -> {
			methodSimpleO1_implemented(i1, ctx)
		}, null);*/
	}
}
