package sk.annotation.library.mapper.fast.processor.data.keys;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import sk.annotation.library.mapper.fast.annotations.MapperFieldConfig;

import java.util.LinkedList;
import java.util.List;

@Data
public class MethodConfigKey {
	final private String forTopMethod;

	@EqualsAndHashCode.Exclude
	private boolean withCustomConfig = false;

	@EqualsAndHashCode.Exclude
	final List<MapperFieldConfig> configurations = new LinkedList<>();
}
