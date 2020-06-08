package architecture.community.i18n.dao;

import java.util.List;
import java.util.Locale;

import architecture.community.i18n.I18nText;

public interface I18nTextDao {

	public abstract void create(List<I18nText> list);
	
	public abstract void update(List<I18nText> list);
	
	public abstract void delete(List<I18nText> list);
	
    public abstract List<I18nText> getTexts();
    
    public abstract List<I18nText> getTexts(Locale locale);	

	public abstract I18nText getText(long textId);
	
    public abstract I18nText findByKeyAndLocale(String key, String locale);

}
