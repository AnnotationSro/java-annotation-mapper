package sk.annotation.library.jam.annotation;


import sk.annotation.library.jam.annotations.*;
import sk.annotation.library.mapper.jam.annotations.*;
import sk.annotation.library.jam.annotations.enums.ConfigErrorReporting;
import sk.annotation.library.jam.annotations.enums.IocScope;

import java.util.List;

@JamMapper(withCustom = {SimpleTest.class, SimpleTest3.class}, defaultErrorConfig = ConfigErrorReporting.WARNINGS_ONLY)
@MapperFieldConfig(
		fieldMapping = {
				@FieldMapping(d = "name", s = "name")
		},
		fieldIgnore = {
				@FieldIgnore(value = "id", ignored = true)
		},
		config = {
				@ConfigGenerator(beanOrField = "", missingAsDestination = ConfigErrorReporting.WARNINGS_ONLY, missingAsSource = ConfigErrorReporting.WARNINGS_ONLY)
		}
)
@EnableSpring(beanName = "daco", scope = IocScope.REQUEST)
@EnableCDI(beanName = "daco", scope = IocScope.DEFAULT)
public abstract class SimpleTest2 {
//	static {
//		SimpleTest.class.getPackage();
//	}

//	@JamMapper(withCustom = {SimpleTest.class, SimpleTest3.class})
//	abstract public DataType2 toDataType(DataType1a value1, DataType1b dataType2, @Return DataType2 ret);

	@MapperFieldConfig(
			fieldMapping = {
					@FieldMapping(d = "DataType1a.id", s = "DataType1a.id"),
					@FieldMapping(d = "DataType1a.parentId", s = "DataType1a.parent.id"),
					@FieldMapping(d = "id", s = "id")
			},
			fieldIgnore = {
					@FieldIgnore(value = "name", ignored = false)
			}
	)
	abstract public DataType1a copy1(DataType1a value1);


	abstract public List<DataType1b> copy(List<DataType1a> value1);
	abstract public List<DataType1b> merge(List<DataType1a> source, @Return List<DataType1b> ret);
//
//	@Override
//	public String toString() {
//		return super.toString();
//	}
//
//	protected void update1(DataType1a methodgenerator, DataType1a dest, @Context("test") Object test) {
//		System.out.println("update1");
//	}
//
//	protected void update2(Object methodgenerator, Object dest) {
//		System.out.println("update2");
//	}
//
//
//	public DataType1a newDataType1a() {
//		return new DataType1a();
//	}
}
