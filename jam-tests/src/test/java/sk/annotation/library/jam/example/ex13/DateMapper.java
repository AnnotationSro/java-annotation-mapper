package sk.annotation.library.jam.example.ex13;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper
public interface DateMapper {
	public java.util.Date clone(java.util.Date obj);
	public java.sql.Date clone(java.sql.Date obj);
	public java.sql.Time clone(java.sql.Time obj);
	public java.sql.Timestamp clone(java.sql.Timestamp obj);

	public java.util.Date convToDate(java.sql.Date obj);
	public java.util.Date convToDate(java.sql.Time obj);
	public java.util.Date convToDate(java.sql.Timestamp obj);

	public java.sql.Date convToSqlDate(java.util.Date obj);
	public java.sql.Date convToSqlDate(java.sql.Time obj);
	public java.sql.Date convToSqlDate(java.sql.Timestamp obj);

	public java.sql.Time convToSqlTime(java.util.Date obj);
	public java.sql.Time convToSqlTime(java.sql.Date obj);
	public java.sql.Time convToSqlTime(java.sql.Timestamp obj);

	public java.sql.Timestamp convToSqlTimestamp(java.util.Date obj);
	public java.sql.Timestamp convToSqlTimestamp(java.sql.Date obj);
	public java.sql.Timestamp convToSqlTimestamp(java.sql.Time obj);
}
