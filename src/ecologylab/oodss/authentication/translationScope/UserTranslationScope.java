package ecologylab.oodss.authentication.translationScope;

import ecologylab.serialization.TranslationScope;

/**
 * Contains all of the information necessary to translate XML objects used in an authenticating
 * server. Use AuthenticationTranslations.get() to acquire a TranslationSpace.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class UserTranslationScope
{
	public static final String	NAME						= "ecologylab.oodss.authentication.User";

	static final Class					TRANSLATIONS[]	=
																							{ ecologylab.oodss.authentication.User.class,
			ecologylab.oodss.authentication.UserWithEmail.class };

	public static TranslationScope get()
	{
		return TranslationScope.get(NAME, TRANSLATIONS);
	}
}
