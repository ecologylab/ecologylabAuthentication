package ecologylab.oodss.logging.translation;

import ecologylab.oodss.logging.AuthenticationOp;
import ecologylab.oodss.logging.translationScope.MixedInitiativeOpClassesProvider;
import ecologylab.serialization.TranslationsClassProvider;

/**
 * Provide base log operation classes for translating a polymorphic list of Ops.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class AuthenticationOpClassesProvider extends MixedInitiativeOpClassesProvider
{
	public static final AuthenticationOpClassesProvider	STATIC_INSTANCE	= new AuthenticationOpClassesProvider();

	protected AuthenticationOpClassesProvider()
	{

	}

	/**
	 * @see ecologylab.serialization.TranslationsClassProvider#specificSuppliedClasses()
	 */
	@Override
	protected Class[] specificSuppliedClasses()
	{
		Class mixedInitiativeOpClasses[] =
		{ AuthenticationOp.class };

		return TranslationsClassProvider.combineClassArrays(super.specificSuppliedClasses(), mixedInitiativeOpClasses);
	}
}
