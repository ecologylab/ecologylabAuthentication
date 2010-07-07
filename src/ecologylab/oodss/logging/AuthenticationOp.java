package ecologylab.oodss.logging;

import ecologylab.oodss.authentication.messages.AuthMessages;
import ecologylab.oodss.logging.MixedInitiativeOp;

/**
 * Logging operation that indicates when a user logs in or out of the server.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class AuthenticationOp extends MixedInitiativeOp implements AuthMessages
{
	/** Login action. */
	public static final String		LOGIN		= "login";

	/** Logout action. */
	public static final String		LOGOUT	= "logout";

	@simpl_scalar private String	username;

	@simpl_scalar private long	currentTimeMillis;

	@simpl_scalar private String	response;

	@simpl_scalar private String	ipAddress;

	public AuthenticationOp()
	{
		super();
	}

	/**
	 * Creates a new AuthenticationOp to indicate that a user logged either in or
	 * out of the server.
	 * 
	 * @param username
	 *           the username of the user.
	 * @param loggingIn
	 *           true if the user is logging in; false for logging out.
	 * @param response
	 *           the response the server gave to the attempt.
	 * @param ipAddress
	 *           the IP address from which the attempt to log in or out
	 *           originated.
	 * @param port
	 *           the port on which the attempt to log in or out was made.
	 */
	public AuthenticationOp(String username, boolean loggingIn, String response,
			String ipAddress)
	{
		this.username = username;

		if (loggingIn)
		{
			action = LOGIN;
		}
		else
		{
			action = LOGOUT;
		}

		this.response = response;

		this.ipAddress = ipAddress;

		this.currentTimeMillis = System.currentTimeMillis();
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @return the response
	 */
	public String getResponse()
	{
		return response;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress()
	{
		return ipAddress;
	}

	/**
	 * @see ecologylab.oodss.logging.MixedInitiativeOp#performAction(boolean)
	 */
	@Override public void performAction(boolean invert)
	{
	}
}
