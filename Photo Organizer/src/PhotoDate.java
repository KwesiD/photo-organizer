


/**
 * This class allows a simple PhotoDate object to be created. This stores 
 * the date of a particular image. This allows for  Currently, there is no need to reference
 * the image it stores the date for. Could probably be replaced with the native Date class.
 * Created for my own convenience, not for better functionality. 
 * @author Kwesi Daniel
 *
 */
public class PhotoDate {
	
	private String month; 
	private String day;
	private String year; 
	private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};//months of the year. Numerical 
																										//value is index+1
	/**
	 * Constructor for the PhotoDate class
	 * @param month
	 * @param day
	 * @param year
	 */
	PhotoDate(String month,String day,String year){
		this.month = month;
		this.day = day;
		this.year = year;
	}
	
	/**
	 * Creates a set format for the date.
	 */
	public String toString(){
		return month + "-" + day + "-" + year;
	}
	
	public String getMonth() {
		return month;
	}

	public String getDay() {
		return day;
	}
	

	public String getYear() {
		return year;
	}
	public String getMonthName(){
		return months[Integer.parseInt(month) - 1];
	}
	
}
