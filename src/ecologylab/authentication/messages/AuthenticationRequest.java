package ecologylab.authentication.messages;

import ecologylab.authentication.User;
import ecologylab.oodss.messages.SendableRequest;

public interface AuthenticationRequest extends SendableRequest
{
	public User getEntry();
}