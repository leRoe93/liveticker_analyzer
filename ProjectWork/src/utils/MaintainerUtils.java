package utils;

import org.joda.time.LocalDate;
import org.joda.time.Years;

public class MaintainerUtils {
	
	
	public static int calculateAge(String birthday) {
		
		String[] birthdayArray = birthday.split("\\.");
		
		LocalDate birthdate = new LocalDate (Integer.parseInt(birthdayArray[2]),
				Integer.parseInt(birthdayArray[1]), 
				Integer.parseInt(birthdayArray[0]));
		LocalDate now = new LocalDate();
		Years age = Years.yearsBetween(birthdate, now);
		return age.getYears();
	}

}
