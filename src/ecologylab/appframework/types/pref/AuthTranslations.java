/**
 * 
 */
package ecologylab.appframework.types.pref;

import ecologylab.appframework.types.prefs.PrefsTranslationsProvider;
import ecologylab.generic.Debug;
import ecologylab.services.authentication.AuthenticationList;
import ecologylab.services.authentication.AuthenticationListXMLImpl;
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
	public static final String NAME = "AUTH_TRANSLATIONS";
	
	/**
	 * What we should be translating to/from xml
	 */
	private static final Class	TRANSLATIONS[]	=
																							{

																							AuthenticationList.class,
			AuthenticationListXMLImpl.class, User.class,

																							};
	
	private static final TranslationScope[] INHERITED_TRANSLATIONS = { PrefsTranslationsProvider.get() };

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
		return TranslationScope.get(NAME, INHERITED_TRANSLATIONS,
				TRANSLATIONS, PrefSetAuthClassProvider.STATIC_INSTANCE.provideClasses());
	}
}
