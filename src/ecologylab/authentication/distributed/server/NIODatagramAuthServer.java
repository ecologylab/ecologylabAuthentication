package ecologylab.authentication.distributed.server;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.List;

import ecologylab.authentication.Authenticatable;
import ecologylab.authentication.AuthenticationList;
import ecologylab.authentication.OnlineAuthenticatorHashMapImpl;
import ecologylab.authentication.User;
import ecologylab.authentication.listener.AuthenticationListener;
import ecologylab.authentication.logging.AuthLogging;
import ecologylab.authentication.logging.AuthenticationOp;
import ecologylab.authentication.messages.AuthMessages;
import ecologylab.authentication.nio.AuthDatagramClientSessionManager;
import ecologylab.authentication.registryobjects.AuthServerRegistryObjects;
import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.server.NIODatagramServer;
import ecologylab.oodss.logging.Logging;
import ecologylab.serialization.SimplTypesScope;

public class NIODatagramAuthServer<A extends User, S extends Scope> extends NIODatagramServer<S>
		implements AuthServerRegistryObjects, AuthMessages, AuthLogging, Authenticatable<A>
{

	/**
	 * Optional Logging listeners may record authentication events, such as users logging-in.
	 */
	private final List<Logging>									logListeners	= new LinkedList<Logging>();

	private final List<AuthenticationListener>	authListeners	= new LinkedList<AuthenticationListener>();

	protected OnlineAuthenticatorHashMapImpl<A>	authenticator	= null;

	public static NIODatagramAuthServer getInstance(int portNumber,
			SimplTypesScope translationScope, Scope objectRegistry, AuthenticationList authList,
			boolean useCompression, int initialPoolSize,
			int minimumPoolSize, int maximumSize)
	{
		NIODatagramAuthServer server = null;

		server = new NIODatagramAuthServer(portNumber,
				translationScope,
				objectRegistry,
				authList,
				useCompression, initialPoolSize, minimumPoolSize, maximumSize);
		return server;
	}

	protected NIODatagramAuthServer(int portNumber,
			SimplTypesScope translationScope,
			S objectRegistry,
			AuthenticationList<A> authList,
			boolean useCompression, int initialPoolSize,
			int minimumPoolSize, int maximumSize)
	{
		super(portNumber, translationScope, objectRegistry, useCompression, initialPoolSize,
				minimumPoolSize, maximumSize);

		// this.applicationObjectScope.put(MAIN_AUTHENTICATABLE, this);
		this.objectRegistry.put(MAIN_AUTHENTICATABLE, this);

		authenticator = new OnlineAuthenticatorHashMapImpl<A>(authList);
	}

	/**
	 * @see ecologylab.oodss.logging.AuthLogging#addLoggingListener(ecologylab.oodss.logging.Logging)
	 */
	@Override
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

	@Override
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
		Object sessionId = authenticator.getSessionId(entry);

		return this.logout(entry, (String) sessionId);
	}

	@Override
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

	@Override
	public boolean isLoggedIn(A entry)
	{
		return authenticator.isLoggedIn(entry);
	}

	@Override
	public boolean login(A entry, String sessionId)
	{
		boolean loginSuccess = authenticator.login(entry, sessionId);

		if (loginSuccess)
		{
			fireLoginEvent(entry.getUserKey(), sessionId);
		}

		return loginSuccess;
	}

	/**
	 * XXX unimplemented
	 * 
	 * @see ecologylab.authentication.Authenticatable#addNewUser(ecologylab.authentication.User)
	 */
	@Override
	public boolean addNewUser(A entry)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * XXX unimplemented
	 * 
	 * @see ecologylab.authentication.Authenticatable#removeExistingUser(ecologylab.authentication.User)
	 */
	@Override
	public boolean removeExistingUser(A entry)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see ecologylab.oodss.distributed.server.NIODatagramServer#generateContextManager(java.lang.String,
	 *      java.nio.channels.SelectionKey, ecologylab.collections.Scope, java.net.SocketAddress)
	 */
	@Override
	protected AuthDatagramClientSessionManager generateContextManager(String sessionId,
			SelectionKey sk, S registryIn, InetSocketAddress address)
	{
		try
		{
			return new AuthDatagramClientSessionManager(sessionId,
					this,
					sk,
					registryIn,
					this,
					authenticator,
					address);
		}
		catch (ClassCastException e)
		{
			debug("ATTEMPT TO USE AuthMessageProcessor WITH A NON-AUTHENTICATING SERVER!");
			e.printStackTrace();
		}

		return null;
	}
}
