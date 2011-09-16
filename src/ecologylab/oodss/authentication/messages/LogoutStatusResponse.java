/*
 * Created on May 12, 2006
 */
package ecologylab.oodss.authentication.messages;

import ecologylab.collections.Scope;
import ecologylab.generic.BooleanSlot;
import ecologylab.oodss.authentication.registryobjects.AuthClientRegistryObjects;
import ecologylab.oodss.messages.ExplanationResponse;
import ecologylab.serialization.annotations.simpl_inherit;

/**
 * Indicates the response from the server regarding an attempt to log out.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
@simpl_inherit public class LogoutStatusResponse extends ExplanationResponse implements AuthMessages,
		AuthClientRegistryObjects
{
	/**
	 * Constructs a new LogoutStatusResponse with the given responseMessage.
	 * 
	 * @param responseMessage
	 *           the response from the server regarding the attempt to log out.
	 */
	public LogoutStatusResponse(String responseMessage)
	{
		super(responseMessage);
	}

	/** No-argument constructor for serialization. */
	public LogoutStatusResponse()
	{
		super();
	}

	/**
	 * Indicates whether or not the attempt to log out was successful.
	 * 
	 * @see ecologylab.oodss.messages.ResponseMessage#isOK()
	 * 
	 * @return true if logout was successful, false otherwise.
	 */
	@Override public boolean isOK()
	{
		return LOGOUT_SUCCESSFUL.equals(this.explanation);
	}

	/**
	 * Sets the LOGIN_STATUS BooleanSlot in the ObjectRegistry for the client to false.
	 * 
	 * @see ecologylab.oodss.messages.ResponseMessage#processResponse(ecologylab.collections.Scope)
	 */
	@Override public void processResponse(Scope objectRegistry)
	{
		((BooleanSlot) objectRegistry.get(LOGIN_STATUS)).value = false;

		objectRegistry.put(LOGIN_STATUS_STRING, explanation);
	}
}
