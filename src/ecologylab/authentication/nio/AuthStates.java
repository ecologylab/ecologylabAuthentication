/*
 * Created on May 15, 2006
 */
package ecologylab.authentication.nio;

import ecologylab.authentication.messages.AuthMessages;
import ecologylab.oodss.distributed.common.BaseStates;

/**
 * Additional states for authenticating clients.
 * 
 * @see ecologylab.oodss.distributed.common.BaseStates
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public interface AuthStates extends BaseStates, AuthMessages
{
	/** Indicates that the client is currently logging in to a server. */
	public static final String	LOGGING_IN	= "Logging in.";

	/** Indicates that the client is currently logged in to a server. */
	public static final String	LOGGED_IN	= "Logged in to server.";
}
