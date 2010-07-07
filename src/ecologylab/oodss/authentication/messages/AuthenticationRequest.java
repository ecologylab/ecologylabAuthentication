package ecologylab.oodss.authentication.messages;

import ecologylab.oodss.authentication.User;
import ecologylab.oodss.messages.SendableRequest;

public interface AuthenticationRequest extends SendableRequest
{
	public User getEntry();
}