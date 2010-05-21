package ecologylab.services.authentication.translationScope;

import ecologylab.xml.TranslationScope;

/**
 * Contains all of the information necessary to translate XML objects used in an authenticating
 * server. Use AuthenticationTranslations.get() to acquire a TranslationSpace.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class UserTranslationScope
{
	public static final String	NAME						= "ecologylab.services.authentication.User";

	static final Class					TRANSLATIONS[]	=
																							{ ecologylab.services.authentication.User.class,
			ecologylab.services.authentication.UserWithEmail.class };

	public static TranslationScope get()
	{
		return TranslationScope.get(NAME, TRANSLATIONS);
	}
}
