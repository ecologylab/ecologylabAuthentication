package ecologylab.oodss.authentication.translationScope;

import ecologylab.appframework.types.pref.AuthTranslations;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.serialization.TranslationScope;

/**
 * Contains all of the information necessary to translate XML objects used in an authenticating
 * server. Use AuthenticationTranslations.get() to acquire a TranslationSpace.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class AuthServerTranslations
{
	protected static final String		NAME											= "authentication";

	static final TranslationScope[]	OTHER_TRANSLATION_SCOPES	=
																														{ DefaultServicesTranslations.get(),
			UserTranslationScope.get()														};

	static final Class							TRANSLATIONS[]						=
																														{
			ecologylab.oodss.authentication.messages.Login.class,
			ecologylab.oodss.authentication.messages.Logout.class,
			ecologylab.oodss.authentication.messages.LoginStatusResponse.class,
			ecologylab.oodss.authentication.messages.LogoutStatusResponse.class,
			ecologylab.oodss.authentication.AuthenticationListXMLImpl.class };

	public static TranslationScope get()
	{
		return TranslationScope.get(NAME, OTHER_TRANSLATION_SCOPES, TRANSLATIONS);
	}

	public static TranslationScope get(String customName, TranslationScope otherSpaceToCompose)
	{
		TranslationScope[] spaces =
		{ DefaultServicesTranslations.get(), AuthTranslations.get(), otherSpaceToCompose };

		return TranslationScope.get(customName, spaces, TRANSLATIONS);
	}
}
