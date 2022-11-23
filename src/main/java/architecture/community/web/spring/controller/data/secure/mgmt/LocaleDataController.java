package architecture.community.web.spring.controller.data.secure.mgmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;

import architecture.community.exception.NotFoundException;
import architecture.community.web.model.DataSourceRequest;
import architecture.community.web.model.Result;
import architecture.ee.service.ConfigService;
import architecture.ee.util.LocaleUtils;

@Controller("community-mgmt-locale-secure-data-controller")
@RequestMapping("/data/secure/mgmt")
public class LocaleDataController {

	private static final Logger log = LoggerFactory.getLogger(ApplicationPropsDataController.class);
	
	@Inject
	@Qualifier("configService")
	private ConfigService configService;
	
	public LocaleDataController() { 
	
	} 
	
	/**
	 * Countries REST API 
	******************************************/

	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/locale/available-list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<LocaleBean> getAvailableLocales( NativeWebRequest request){ 
		
	
		Locale locales[] = Locale.getAvailableLocales();
		
		Arrays.sort(locales, new Comparator<Locale>() {
		    public int compare(Locale locale1, Locale locale2) {
		    	return locale1.getDisplayName().compareTo(locale2.getDisplayName());
		    }
		});
		List<LocaleBean> list = new ArrayList<LocaleBean>(locales.length);
		for( Locale locale : locales)
		{
			if(StringUtils.isNotEmpty(locale.getCountry())&& StringUtils.isNotEmpty(locale.getLanguage()))
				list.add(LocaleBean.build(locale));
		}
		return list ;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/locale/{languageTag}/timezone/list.json", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public List<TimeZoneBean> getAvailableTimezones( @PathVariable String languageTag, NativeWebRequest request){   
		Locale localeToUse = Locale.forLanguageTag(languageTag);
		String[][] timezones = LocaleUtils.getTimeZoneList(localeToUse); 
		List<TimeZoneBean> list = new ArrayList<TimeZoneBean>(timezones.length);
		for( String [] row: timezones) {
			String timezoneID = row[0];
			TimeZone timezone = TimeZone.getTimeZone(timezoneID);
			list.add(TimeZoneBean.build(timezone));
		} 
		return list ;
	}
	
	@Secured({ "ROLE_ADMINISTRATOR", "ROLE_SYSTEM", "ROLE_DEVELOPER"})
	@RequestMapping(value = "/locale/save-or-update.json", method = { RequestMethod.POST, RequestMethod.GET})
	@ResponseBody
	public Result saveOrUpdate (
		@RequestBody DataSourceRequest dataSourceRequest,
		NativeWebRequest request) throws NotFoundException {	
				 
		String languageTag = dataSourceRequest.getDataAsString("locale", null);
		String timeZoneID = dataSourceRequest.getDataAsString("timeZone", null); 
		TimeZone newTimeZone = null;
		Locale newLocale = null; 
		if( StringUtils.isNotEmpty(languageTag)) { 
			newLocale = Locale.forLanguageTag(languageTag);
		}
		if( StringUtils.isNotEmpty(timeZoneID)) { 
			newTimeZone = TimeZone.getTimeZone(timeZoneID); 
		}
		if( newLocale != null )
			configService.setLocale(newLocale);
		if( timeZoneID != null )
			configService.setTimeZone(newTimeZone);
		
		return Result.newResult();
	}
	
	
	public static class TimeZoneBean {
		
		@JsonIgnore
		private TimeZone timeZoneToUse;
		
		public String getDisplayName() {
			return timeZoneToUse.getDisplayName();
		} 

		public Integer getDSTSavings() {
			return timeZoneToUse.getDSTSavings();
		} 
		
		public Integer getRawOffset() {
			return timeZoneToUse.getRawOffset();
		} 
		
		public String getID() {
			return timeZoneToUse.getID();
		} 
		
		public TimeZoneBean(TimeZone timeZone) { 
			this.timeZoneToUse = timeZone;
		}

		public static TimeZoneBean build(TimeZone timeZone) {  
			return new TimeZoneBean(timeZone);
		}
	}
	

	public static class LocaleBean {
		
		@JsonIgnore
		private Locale localeToUse;
		 
		public LocaleBean(Locale localeToUse) {
			this.localeToUse = localeToUse;
		}

		public String getDisplayName() {
			if( localeToUse == null )
				return null;
			return localeToUse.getDisplayName();
		} 
		
		public String getDisplayLanguage() {
			if( localeToUse == null )
				return null;
			return localeToUse.getDisplayLanguage();
		} 
		
		public String getDisplayCountry() {
			if( localeToUse == null )
				return null;
			return localeToUse.getDisplayCountry();
		}  
		
		public String getLanguage() {
			if( localeToUse == null )
				return null;
			return localeToUse.getLanguage();
		}  
		
		public String getCountry() {
			if( localeToUse == null )
				return null;
			return localeToUse.getCountry();
		}
		
		public String getVariant() {
			if( localeToUse == null )
				return null;
			return localeToUse.getVariant();
		}
		
		public String getLanguageTag() {
			if( localeToUse == null )
				return null;
			return localeToUse.toLanguageTag();
		}
		public static LocaleBean build(Locale locale) {  
			return new LocaleBean(locale);
		}
	}
}
