package ecologylab.services.authentication.nio;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import ecologylab.collections.Scope;
import ecologylab.services.authentication.OnlineAuthenticator;
import ecologylab.services.authentication.messages.AuthMessages;
import ecologylab.services.authentication.messages.AuthenticationRequest;
import ecologylab.services.authentication.messages.Logout;
import ecologylab.services.authentication.registryobjects.AuthServerRegistryObjects;
import ecologylab.services.distributed.common.ServerConstants;
import ecologylab.services.distributed.server.NIOServerProcessor;
import ecologylab.services.distributed.server.clientsessionmanager.DatagramClientSessionManager;
import ecologylab.services.logging.AuthLogging;
import ecologylab.services.logging.AuthenticationOp;
import ecologylab.services.messages.BadSemanticContentResponse;
import ecologylab.services.messages.ExplanationResponse;
import ecologylab.services.messages.RequestMessage;
import ecologylab.services.messages.ResponseMessage;

/**
 * Stores information about the connection context for the client, including authentication status.
 * Only executes requests from an authenticated client. Should be extended for more specific
 * implementations. Handles accumulating incoming messages and translating them into RequestMessage
 * objects.
 * 
 * @see ecologylab.services.distributed.server.clientsessionmanager.ClientSessionManager
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class AuthDatagramClientSessionManager extends DatagramClientSessionManager implements
		ServerConstants, AuthServerRegistryObjects, AuthMessages
{
	private boolean							loggedIn				= false;

	private AuthLogging					servicesServer	= null;

	private OnlineAuthenticator	authenticator		= null;

	/**
	 * Constructs a new AuthClientSessionManager on a server to handle authenticating client requests.
	 * 
	 * @param token
	 * @param maxPacketSize
	 * @param server
	 * @param frontend
	 * @param socket
	 * @param translationScope
	 * @param registry
	 * @param servicesServer
	 */
	@SuppressWarnings("unchecked")
	public AuthDatagramClientSessionManager(String token, NIOServerProcessor frontend,
			SelectionKey sk, Scope registry, AuthLogging servicesServer,
			OnlineAuthenticator authenticator, SocketAddress address)
	{
		super(token, frontend, sk, registry, address);

		this.servicesServer = servicesServer;
		this.authenticator = authenticator;
	}

	/**
	 * Calls performService on the given RequestMessage using the local ObjectRegistry, if the client
	 * has been authenticated, or if the request is to log in. Can be overridden by subclasses to
	 * provide more specialized functionality.
	 * 
	 * @param requestMessage
	 * @return
	 */
	@Override
	protected ResponseMessage performService(RequestMessage requestMessage, InetAddress address)
	{
		ResponseMessage response;

		// if not logged in yet, make sure they log in first
		if (!loggedIn || !this.authenticator.sessionValid(this.sessionId))
		{
			if (requestMessage instanceof AuthenticationRequest)
			{
				// since this is a Login message, perform it.
				response = super.performService(requestMessage, address);

				if (response.isOK())
				{
					// mark as logged in, and add to the authenticated
					// clients
					// in the object registry
					loggedIn = true;
				}

				// tell the server to log it
				servicesServer.fireLoggingEvent(new AuthenticationOp(
						((AuthenticationRequest) requestMessage).getEntry().getUserKey(), true,
						((ExplanationResponse) response).getExplanation(), address.toString()));
			}
			else
			{ // otherwise we consider it bad!
				response = new BadSemanticContentResponse(REQUEST_FAILED_NOT_AUTHENTICATED);
			}

		}
		else
		{
			response = super.performService(requestMessage, address);

			if (requestMessage instanceof Logout)
			{
				// tell the server to log it
				servicesServer.fireLoggingEvent(new AuthenticationOp(((Logout) requestMessage).getEntry()
						.getUserKey(), false, ((ExplanationResponse) response).getExplanation(),
						address.toString()));
			}
		}

		// return the response message
		return response;
	}
}
