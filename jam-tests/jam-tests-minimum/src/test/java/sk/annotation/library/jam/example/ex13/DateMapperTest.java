package sk.annotation.library.jam.example.ex13;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sk.annotation.library.jam.utils.MapperUtil;

import java.sql.Timestamp;
import java.util.Date;

public class DateMapperTest {

    DateMapper mapper = MapperUtil.getMapper(DateMapper.class);


	@Test
	public void test_utilDate_to_utilDate() {
		Assertions.assertEquals(null, mapper.utilDate_to_utilDate(null));

		java.util.Date i = new Date();
		java.util.Date o = mapper.utilDate_to_utilDate(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlDate_to_sqlDate() {
		Assertions.assertEquals(null, mapper.sqlDate_to_sqlDate(null));

		java.sql.Date i = new java.sql.Date(System.currentTimeMillis());
		java.sql.Date o = mapper.sqlDate_to_sqlDate(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTime_to_sqlTime() {
		Assertions.assertEquals(null, mapper.sqlTime_to_sqlTime(null));

		java.sql.Time i = new java.sql.Time(System.currentTimeMillis());
		java.sql.Time o = mapper.sqlTime_to_sqlTime(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTimestamp_to_sqlTimestamp() {
		Assertions.assertEquals(null, mapper.sqlTimestamp_to_sqlTimestamp(null));

		Timestamp i = new Timestamp(System.currentTimeMillis());
		Timestamp o = mapper.sqlTimestamp_to_sqlTimestamp(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlDate_to_utilDate() {
		Assertions.assertEquals(null, mapper.sqlDate_to_utilDate(null));

		java.sql.Date i = new java.sql.Date(System.currentTimeMillis());
		java.util.Date o = mapper.sqlDate_to_utilDate(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTime_to_utilDate() {
		Assertions.assertEquals(null, mapper.sqlTime_to_utilDate(null));

		java.sql.Time i = new java.sql.Time(System.currentTimeMillis());
		java.util.Date o = mapper.sqlTime_to_utilDate(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTimestamp_to_utilDate() {
		Assertions.assertEquals(null, mapper.sqlTimestamp_to_utilDate(null));

		java.sql.Timestamp i = new java.sql.Timestamp(System.currentTimeMillis());
		java.util.Date o = mapper.sqlTimestamp_to_utilDate(i);
		Assertions.assertNotEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_utilDate_to_sqlDate() {
		Assertions.assertEquals(null, mapper.utilDate_to_sqlDate(null));

		java.util.Date i = new java.util.Date(System.currentTimeMillis());
		java.sql.Date o = mapper.utilDate_to_sqlDate(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTime_to_sqlDate() {
		Assertions.assertEquals(null, mapper.sqlTime_to_sqlDate(null));

		java.sql.Time i = new java.sql.Time(System.currentTimeMillis());
		java.sql.Date o = mapper.sqlTime_to_sqlDate(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTimestamp_to_sqlDate() {
		Assertions.assertEquals(null, mapper.sqlTimestamp_to_sqlDate(null));

		java.sql.Timestamp i = new java.sql.Timestamp(System.currentTimeMillis());
		java.sql.Date o = mapper.sqlTimestamp_to_sqlDate(i);
		Assertions.assertNotEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_utilDate_to_sqlTime() {
		Assertions.assertEquals(null, mapper.utilDate_to_sqlTime(null));

		java.util.Date i = new java.util.Date(System.currentTimeMillis());
		java.sql.Time o = mapper.utilDate_to_sqlTime(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlDate_to_sqlTime() {
		Assertions.assertEquals(null, mapper.sqlDate_to_sqlTime(null));

		java.sql.Date i = new java.sql.Date(System.currentTimeMillis());
		java.sql.Time o = mapper.sqlDate_to_sqlTime(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTimestamp_to_sqlTime() {
		Assertions.assertEquals(null, mapper.sqlTimestamp_to_sqlTime(null));

		java.sql.Timestamp i = new java.sql.Timestamp(System.currentTimeMillis());
		java.sql.Time o = mapper.sqlTimestamp_to_sqlTime(i);
		Assertions.assertNotEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_utilDate_to_sqlTimestamp() {
		Assertions.assertEquals(null, mapper.utilDate_to_sqlTimestamp(null));

		java.util.Date i = new java.util.Date(System.currentTimeMillis());
		java.sql.Timestamp o = mapper.utilDate_to_sqlTimestamp(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());

	}

	@Test
	public void test_sqlDate_to_sqlTimestamp() {
		Assertions.assertEquals(null, mapper.sqlDate_to_sqlTimestamp(null));

		java.sql.Date i = new java.sql.Date(System.currentTimeMillis());
		java.sql.Timestamp o = mapper.sqlDate_to_sqlTimestamp(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

	@Test
	public void test_sqlTime_to_sqlTimestamp() {
		Assertions.assertEquals(null, mapper.sqlTime_to_sqlTimestamp(null));

		java.sql.Time i = new java.sql.Time(System.currentTimeMillis());
		java.sql.Timestamp o = mapper.sqlTime_to_sqlTimestamp(i);
		Assertions.assertEquals(i, o);
		Assertions.assertNotSame(i, o);
		Assertions.assertEquals(i.getTime(), o.getTime());
	}

//	public java.sql.Date clone(java.sql.Date obj);
//	public java.sql.Time clone(java.sql.Time obj);
//	public java.sql.Timestamp clone(java.sql.Timestamp obj);
//
//	public java.util.Date convToDate(java.sql.Date obj);
//	public java.util.Date convToDate(java.sql.Time obj);
//	public java.util.Date convToDate(java.sql.Timestamp obj);
//
//	public java.sql.Date convToSqlDate(java.util.Date obj);
//	public java.sql.Date convToSqlDate(java.sql.Time obj);
//	public java.sql.Date convToSqlDate(java.sql.Timestamp obj);
//
//	public java.sql.Time convToSqlTime(java.util.Date obj);
//	public java.sql.Time convToSqlTime(java.sql.Date obj);
//	public java.sql.Time convToSqlTime(java.sql.Timestamp obj);
//
//	public java.sql.Timestamp convToSqlTimestamp(java.util.Date obj);
//	public java.sql.Timestamp convToSqlTimestamp(java.sql.Date obj);
//	public java.sql.Timestamp convToSqlTimestamp(java.sql.Time obj);

}
