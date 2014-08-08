package ecologylab.authentication.translationScope;

import ecologylab.appframework.types.pref.AuthTranslations;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.serialization.SimplTypesScope;

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

	static final SimplTypesScope[]	OTHER_TRANSLATION_SCOPES	=
																														{ DefaultServicesTranslations.get(),
			UserTranslationScope.get()														};

	static final Class							TRANSLATIONS[]						=
																														{
			ecologylab.authentication.messages.Login.class,
			ecologylab.authentication.messages.Logout.class,
			ecologylab.authentication.messages.LoginStatusResponse.class,
			ecologylab.authentication.messages.LogoutStatusResponse.class,
			ecologylab.authentication.AuthenticationListXMLImpl.class };

	public static SimplTypesScope get()
	{
		return SimplTypesScope.get(NAME, OTHER_TRANSLATION_SCOPES, TRANSLATIONS);
	}

	public static SimplTypesScope get(String customName, SimplTypesScope otherSpaceToCompose)
	{
		SimplTypesScope[] spaces =
		{ DefaultServicesTranslations.get(), AuthTranslations.get(), otherSpaceToCompose };

		return SimplTypesScope.get(customName, spaces, TRANSLATIONS);
	}
}
