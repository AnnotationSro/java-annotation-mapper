package sk.annotation.library.jam.example.ex13;

import sk.annotation.library.jam.annotations.Mapper;

@Mapper
public interface DateMapper {
	public java.util.Date utilDate_to_utilDate(java.util.Date obj);
	public java.sql.Date sqlDate_to_sqlDate(java.sql.Date obj);
	public java.sql.Time sqlTime_to_sqlTime(java.sql.Time obj);
	public java.sql.Timestamp sqlTimestamp_to_sqlTimestamp(java.sql.Timestamp obj);

	public java.util.Date sqlDate_to_utilDate(java.sql.Date obj);
	public java.util.Date sqlTime_to_utilDate(java.sql.Time obj);
	public java.util.Date sqlTimestamp_to_utilDate(java.sql.Timestamp obj);

	public java.sql.Date utilDate_to_sqlDate(java.util.Date obj);
	public java.sql.Date sqlTime_to_sqlDate(java.sql.Time obj);
	public java.sql.Date sqlTimestamp_to_sqlDate(java.sql.Timestamp obj);

	public java.sql.Time utilDate_to_sqlTime(java.util.Date obj);
	public java.sql.Time sqlDate_to_sqlTime(java.sql.Date obj);
	public java.sql.Time sqlTimestamp_to_sqlTime(java.sql.Timestamp obj);

	public java.sql.Timestamp utilDate_to_sqlTimestamp(java.util.Date obj);
	public java.sql.Timestamp sqlDate_to_sqlTimestamp(java.sql.Date obj);
	public java.sql.Timestamp sqlTime_to_sqlTimestamp(java.sql.Time obj);
}
