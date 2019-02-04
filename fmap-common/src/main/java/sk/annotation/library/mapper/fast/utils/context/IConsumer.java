package sk.annotation.library.mapper.fast.utils.context;

public interface IConsumer<T> {
	void accept(T t) throws Exception;
}
