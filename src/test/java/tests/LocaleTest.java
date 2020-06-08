package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.ee.util.LocaleUtils;

public class LocaleTest {
	private static final Logger logger = LoggerFactory.getLogger(LocaleTest.class);
	public LocaleTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testTimezone() { 
		
		System.out.println(architecture.community.query.CustomQueryService.class.getName() );
		
		 
		Locale.Builder builder = new Locale.Builder();

		Locale locales [] = Locale.getAvailableLocales();
		Arrays.sort(locales, new Comparator<Locale>() {
		    public int compare(Locale locale1, Locale locale2) {
		    	return locale1.getDisplayName().compareTo(locale2.getDisplayName());
		    }
		});
		List<Locale> list = new ArrayList<Locale>(locales.length);
		for( Locale locale : locales)
		{
			if(StringUtils.isNotEmpty(locale.getCountry())&& StringUtils.isNotEmpty(locale.getLanguage()))
			{
				list.add(locale);
				logger.debug("{}    {}/{} - Language : {} Country : {} Variant : {}", locale.getDisplayName(), locale.getDisplayLanguage(), locale.getDisplayCountry(), locale.getLanguage(), locale.getCountry(), locale.getVariant());
			}
			
		}
		logger.debug("Available locales : {}", list);
		
		String[] IDs = TimeZone.getAvailableIDs();
		for( String ID : IDs ){
			TimeZone zone = TimeZone.getTimeZone(ID);  
			logger.debug("{} {} {}", zone.toZoneId(), zone.getRawOffset(), zone.getDisplayName());
		}
		
		
		Locale localeToUse = Locale.CANADA;
		String[][] timezones = LocaleUtils.getTimeZoneList(localeToUse);
		
		for( String [] row: timezones) {
			String timezoneID = row[0];
			logger.debug("{} {}", localeToUse.getDisplayName(), TimeZone.getTimeZone(timezoneID).getDisplayName());
		} 
		
		Locale locale = new Locale("de", "DE", "");
		logger.debug("{}", locale.getDisplayName());
	}
	
	public static class LanguageAndCountry {
		
		String displayLanguage;
		
		String displayCountry;
		
		String language;
		
		String country; 
		
		public LanguageAndCountry(String language, String country, String displayLanguage, String displayCountry) {
			this.language = language;
			this.country = country;
			this.displayLanguage = displayLanguage;
			this.displayCountry = displayCountry;
		}
		public String getDisplayLanguage() {
			return displayLanguage;
		} 
		public void setDisplayLanguage(String displayLanguage) {
			this.displayLanguage = displayLanguage;
		} 
		public String getDisplayCountry() {
			return displayCountry;
		} 
		public void setDisplayCountry(String displayCountry) {
			this.displayCountry = displayCountry;
		} 
		public String getLanguage() {
			return language;
		} 
		public void setLanguage(String language) {
			this.language = language;
		} 
		public String getCountry() {
			return country;
		} 
		public void setCountry(String country) {
			this.country = country;
		} 

		public static LanguageAndCountry build(Locale locale) {
			return new LanguageAndCountry(locale.getLanguage(), locale.getCountry(), locale.getDisplayLanguage(), locale.getDisplayCountry());
		}
	}
}
