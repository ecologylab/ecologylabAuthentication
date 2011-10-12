package ecologylab.oodss.authentication.nio;

import java.io.IOException;
import java.net.InetSocketAddress;

import ecologylab.collections.Scope;
import ecologylab.generic.BooleanSlot;
import ecologylab.oodss.authentication.AuthConstants;
import ecologylab.oodss.authentication.User;
import ecologylab.oodss.authentication.messages.AuthMessages;
import ecologylab.oodss.authentication.messages.Login;
import ecologylab.oodss.authentication.messages.Logout;
import ecologylab.oodss.authentication.registryobjects.AuthClientRegistryObjects;
import ecologylab.oodss.distributed.client.NIODatagramClient;
import ecologylab.oodss.distributed.exception.MessageTooLargeException;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.serialization.SimplTypesScope;

/**
 * Authentication subclass of NIODatagram Client.
 * @author bilhamil
 *
 * @param <S>  application scope type parameter
 */
public class NIODatagramAuthClient<S extends Scope> extends NIODatagramClient<S> implements
AuthClientRegistryObjects, AuthConstants, AuthMessages
{

	/** The username / password information supplied by the user. */
	protected User	entry			= null;

	/** Indicates that this is logging in. */
	private boolean							loggingIn	= false;

	/** Indicates that this is logging out. */
	private boolean							loggingOut	= false;

	/**
	 * Authentication client constructor. Initializes new datagram client and 
	 * starts the connection process. Specifies the authentication
	 * entry through the entry parameter.
	 * 
	 * @param serverAddress
	 * @param localAddress local address of the interface that you want to establish the client on
	 * @param translationScope
	 * @param objectRegistry application scope
	 * @param entry authentication credentials for this client
	 * @param useCompression whether or not to use compression
	 * @param timeout timeout of messages that are not responded to
	 */
	public NIODatagramAuthClient(InetSocketAddress serverAddress,
			InetSocketAddress localAddress, SimplTypesScope translationScope,
			S objectRegistry, User entry, boolean useCompression, int timeout)
	{
		super(serverAddress, localAddress, translationScope, objectRegistry, useCompression, timeout);
		
		objectRegistry.put(LOGIN_STATUS, new BooleanSlot(false));
		objectRegistry.put(LOGIN_STATUS_STRING, null);
		
		this.entry = entry;
	}

	/**
	 * Authentication client constructor. Initializes new datagram client and 
	 * starts the connection process. Specifies the authentication
	 * entry through the entry parameter.
	 * 
	 * @param serverAddress
	 * @param translationScope
	 * @param objectRegistry application scope
	 * @param entry authentication credentials for this client
	 * @param useCompression whether or not to use compression
	 * @param timeout timeout of messages that are not responded to
	 */
	public NIODatagramAuthClient(InetSocketAddress serverAddress,
										  SimplTypesScope translationScope, S objectRegistry,
										  User entry, boolean useCompression, int timeout)
	{
		super(serverAddress, translationScope, objectRegistry, useCompression, timeout);
		
		objectRegistry.put(LOGIN_STATUS, new BooleanSlot(false));
		objectRegistry.put(LOGIN_STATUS_STRING, null);
		
		this.entry = entry;
	}
	
	/**
	 * Authentication client constructor. Initializes new datagram client and 
	 * starts the connection process. Specifies the authentication
	 * entry through the entry parameter.
	 * 
	 * @param serverAddress
	 * @param translationScope
	 * @param objectRegistry application scope
	 * @param useCompression whether or not to use compression
	 * @param timeout timeout of messages that are not responded to
	 */
	public NIODatagramAuthClient(InetSocketAddress serverAddress,
			  SimplTypesScope translationScope, S objectRegistry, boolean useCompression,
			  int timeout)
	{
		this(serverAddress, translationScope, objectRegistry, null, useCompression, timeout);
	}
	
	/**
	 * Authentication client constructor. Initializes new datagram client and 
	 * starts the connection process. Specifies the authentication
	 * entry through the entry parameter.
	 * 
	 * @param serverAddress
	 * @param localAddress local address of the interface that you want to establish the client on
	 * @param translationScope
	 * @param objectRegistry application scope
	 * @param useCompression whether or not to use compression
	 * @param timeout timeout of messages that are not responded to
	 */
	public NIODatagramAuthClient(InetSocketAddress serverAddress,
			InetSocketAddress localAddress, SimplTypesScope translationScope,
			S objectRegistry, boolean useCompression, int timeout)
	{
		this(serverAddress, localAddress, translationScope, objectRegistry, null, useCompression, timeout);
	}
	
	/**
	 * @param entry
	 *           The entry to set.
	 */
	public void setEntry(User entry)
	{
		this.entry = entry;
	}

	/**
	 * Attempts to connect to the server using the AuthenticationListEntry that
	 * is associated with the client's side of the connection. Does not block for
	 * connection.
	 * 
	 * @throws IOException
	 * @throws MessageTooLargeException
	 */
	public boolean login() throws IOException, MessageTooLargeException
	{
		// if we have an entry (username + password), then we can try to connect
		// to the server.
		if (entry != null)
		{
			loggingOut = false;
			loggingIn = true;

			// Login response will handle changing the LOGIN_STATUS
			sendLoginMessage();				
		}
		else
		{
			debug("ENTRY NOT SET!");
		}

		return isLoggedIn();
	}

	/**
	 * Attempts to log out of the server using the AuthenticationListEntry that
	 * is associated with the client's side of the connection. Blocks until a
	 * response is received or until LOGIN_WAIT_TIME passes, whichever comes
	 * first.
	 * 
	 * @throws IOException
	 * @throws MessageTooLargeException
	 */
	protected boolean logout()
	{
		// if we have an entry (username + password), then we can try to logout of
		// the server.
		if (entry != null)
		{
			loggingIn = false;
			loggingOut = true;

			// Login response will handle changing the LOGIN_STATUS
			sendLogoutMessage();
		}

		return isLoggedIn();
	}

	/**
	 * Sends a Logout message to the server; may be overridden by subclasses that
	 * need to add addtional information to the Logout message.
	 * 
	 * @throws MessageTooLargeException
	 * 
	 */
	protected ResponseMessage sendLogoutMessage() 
	{
		return this.sendMessage(new Logout(entry));
	}

	/**
	 * Sends a Login message to the server; may be overridden by subclasses that
	 * need to add addtional information to the Login message.
	 * 
	 * @throws MessageTooLargeException
	 * 
	 */
	protected ResponseMessage sendLoginMessage() throws IOException,
			MessageTooLargeException
	{
		ResponseMessage temp = this.sendMessage(new Login(entry));

		return temp;
	}

	/**
	 * @return Returns the loggingIn.
	 */
	public boolean isLoggingIn()
	{
		return loggingIn;
	}

	/**
	 * @return Returns the loggingOut.
	 */
	public boolean isLoggingOut()
	{
		return loggingOut;
	}

	/**
	 * @return The response message from the server regarding the last attempt to
	 *         log in; if login fails, will indicate why.
	 */
	public String getExplanation()
	{
		String temp = (String) objectRegistry.get(LOGIN_STATUS_STRING);

		if (temp == null)
		{
			return "";
		}

		return temp;
	}

	/**
	 * @return Returns whether or not this client is logged in to a server.
	 */
	public boolean isLoggedIn()
	{
		return ((BooleanSlot) objectRegistry.get(LOGIN_STATUS)).value;
	}

}
