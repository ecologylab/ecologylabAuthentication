/**
 * 
 */
package ecologylab.oodss.distributed.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.List;

import ecologylab.collections.Scope;
import ecologylab.oodss.authentication.Authenticatable;
import ecologylab.oodss.authentication.OnlineAuthenticator;
import ecologylab.oodss.authentication.User;
import ecologylab.oodss.authentication.listener.AuthenticationListener;
import ecologylab.oodss.authentication.messages.AuthMessages;
import ecologylab.oodss.authentication.nio.AuthClientSessionManager;
import ecologylab.oodss.authentication.registryobjects.AuthServerRegistryObjects;
import ecologylab.oodss.authentication.translationScope.AuthServerTranslations;
import ecologylab.oodss.exceptions.SaveFailedException;
import ecologylab.oodss.logging.AuthLogging;
import ecologylab.oodss.logging.AuthenticationOp;
import ecologylab.oodss.logging.Logging;
import ecologylab.serialization.SimplTypesScope;

/**
 * An authenticating server that uses NIO and two threads (one for handling IO, the other for
 * handling interfacing with messages).
 * 
 * Any clients attempting to communicate with this server must either first provide a Login request,
 * or otherwise have previously been logged in; otherwise, no requests are processed from the
 * client.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class DoubleThreadedAuthNIOServer<A extends User> extends
		DoubleThreadedNIOServer implements AuthServerRegistryObjects, AuthMessages, AuthLogging,
		Authenticatable<A>
{
	/**
	 * Optional Logging listeners may record authentication events, such as users logging-in.
	 */
	private List<Logging>									logListeners	= new LinkedList<Logging>();

	private List<AuthenticationListener>	authListeners	= new LinkedList<AuthenticationListener>();

	protected OnlineAuthenticator<A>			authenticator	= null;

	/**
	 * @param portNumber
	 * @param inetAddress
	 * @param translationScope
	 * @param objectRegistry
	 * @throws IOException
	 * @throws BindException
	 */
	public DoubleThreadedAuthNIOServer(int portNumber, InetAddress[] inetAddress,
			SimplTypesScope requestTranslationSpace, Scope objectRegistry, int idleConnectionTimeout,
			int maxPacketSize, OnlineAuthenticator<A> authenticator) throws IOException, BindException
	{
		// MODEL for translation space
		super(portNumber, inetAddress, AuthServerTranslations.get("double_threaded_auth "
				+ inetAddress[0].toString()
				+ ":"
				+ portNumber, requestTranslationSpace), objectRegistry, idleConnectionTimeout,
				maxPacketSize);

		this.applicationObjectScope.put(MAIN_AUTHENTICATABLE, this);

		this.authenticator = authenticator;
	}

	/**
	 * 
	 * @param sessionId
	 * @param translationScope
	 * @param registry
	 * @return
	 */
	@Override
	protected AuthClientSessionManager generateContextManager(String sessionId, SelectionKey sk,
			SimplTypesScope translationScope, Scope registry)
	{
		try
		{
			return new AuthClientSessionManager(sessionId, maxMessageSize, getBackend(), this, sk,
					translationScope, registry, this, authenticator);
		}
		catch (ClassCastException e)
		{
			debug("ATTEMPT TO USE AuthMessageProcessor WITH A NON-AUTHENTICATING SERVER!");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @see ecologylab.oodss.logging.AuthLogging#addLoggingListener(ecologylab.oodss.logging.Logging)
	 */
	public void addLoggingListener(Logging log)
	{
		logListeners.add(log);
	}

	public void addAuthenticationListener(AuthenticationListener authListener)
	{
		authListeners.add(authListener);
	}

	protected void fireLogoutEvent(String username, String sessionId)
	{
		for (AuthenticationListener a : authListeners)
		{
			a.userLoggedOut(username, sessionId);
		}
	}

	protected void fireLoginEvent(String username, String sessionId)
	{
		for (AuthenticationListener a : authListeners)
		{
			a.userLoggedIn(username, sessionId);
		}
	}

	public void fireLoggingEvent(AuthenticationOp op)
	{
		for (Logging logListener : logListeners)
		{
			logListener.logAction(op);
		}
	}

	/**
	 * Force logout of an entry; do not require the session id.
	 * 
	 * @param entry
	 * @return
	 */
	protected boolean logout(A entry)
	{
		String sessionId = authenticator.getSessionId(entry);

		return this.logout(entry, sessionId);
	}

	public boolean logout(A entry, String sessionId)
	{
		boolean logoutSuccess = authenticator.logout(entry, sessionId);

		if (logoutSuccess)
		{
			debug(entry.getUserKey() + " has been logged out.");
			fireLogoutEvent(entry.getUserKey(), sessionId);
		}

		return logoutSuccess;
	}

	public boolean isLoggedIn(A entry)
	{
		return authenticator.isLoggedIn(entry);
	}

	public boolean login(A entry, String sessionId)
	{
		boolean loginSuccess = authenticator.login(entry, sessionId);

		if (loginSuccess)
		{
			fireLoginEvent(entry.getUserKey(), sessionId);
		}

		return loginSuccess;
	}

	private void remove(String sessionId)
	{
		authenticator.logoutBySessionId(sessionId);
	}

	/**
	 * Ensure that the user associated with sc has been logged out of the authenticator, then call
	 * super.invalidate().
	 * 
	 * @see ecologylab.oodss.distributed.server.DoubleThreadedNIOServer#invalidate(java.lang.Object,
	 *      ecologylab.oodss.distributed.impl.NIOServerIOThread, java.nio.channels.SocketChannel)
	 */
	@Override
	public boolean invalidate(String sessionId, boolean forcePermanent)
	{
		boolean retVal = super.invalidate(sessionId, forcePermanent);

		if (retVal)
		{
			this.remove((String) sessionId);
		}

		return retVal;
	}

	/**
	 * @see ecologylab.oodss.authentication.Authenticatable#addNewUser(ecologylab.oodss.authentication.User)
	 */
	public boolean addNewUser(A entry)
	{
		try
		{
			return this.authenticator.addUser(entry);
		}
		catch (SaveFailedException e)
		{
			e.printStackTrace();
		}
		return running;
	}

	/**
	 * @see ecologylab.oodss.authentication.Authenticatable#removeExistingUser(ecologylab.oodss.authentication.User)
	 */
	public boolean removeExistingUser(A entry)
	{
		try
		{
			return this.authenticator.removeUser(entry);
		}
		catch (SaveFailedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return running;
	}
}
