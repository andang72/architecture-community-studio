package architecture.community.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import architecture.ee.i18n.Localizer;

public class CommunityLogLocalizer {
	
	private static Localizer localizer = null;

	static {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(CommunityLogLocalizer.class.getName());
			localizer = new Localizer(bundle);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static String getMessage(String id) throws MissingResourceException {
		return localizer.getMessage(id);
	}

	public static String getMessage(int id) throws MissingResourceException {
		return localizer.getMessage(id);
	}

	public static String format(String id, Object... args) {
		return localizer.format(id, args);
	}

	public static String format(int id, Object... args) {
		return localizer.format(id, args);
	}
	
}
