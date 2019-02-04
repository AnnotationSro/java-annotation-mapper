package sk.annotation.library.mapper.fast.utils.context;

public interface IFunction<IN,OUT> {
	public OUT apply(IN in) throws Exception;
}
