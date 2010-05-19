/**
 * 
 */
package ecologylab.appframework.types.pref;

import ecologylab.appframework.types.prefs.PrefTranslations;
import ecologylab.generic.Debug;
import ecologylab.services.authentication.AuthenticationList;
import ecologylab.services.authentication.User;
import ecologylab.xml.TranslationScope;

/**
 * Translations for the pref/meta_pref system.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class AuthTranslations extends Debug
{
	/**
	 * Package name
	 */
	private static final String	PACKAGE_NAME		= "ecologylab.appframework.types.prefs";

	/**
	 * What we should be translating to/from xml
	 */
	private static final Class	TRANSLATIONS[]	=
																							{

																							PrefAuthList.class, AuthenticationList.class,
			User.class,

																							};

	/**
	 * Just prevent anyone from new'ing this.
	 */
	private AuthTranslations()
	{
	}

	/**
	 * Get the translation space
	 */
	public static TranslationScope get()
	{
		return TranslationScope.get(PACKAGE_NAME, PrefTranslations.get(), TRANSLATIONS);
	}
}
