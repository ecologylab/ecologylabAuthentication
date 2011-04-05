/*
 * Created on Mar 30, 2006
 */
package ecologylab.oodss.authentication;

import ecologylab.serialization.simpl_inherit;

/**
 * Subclass of User that provides an email address as auxiliary information. Email address may be
 * stored in the database, but is not used as a key.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
@simpl_inherit
public class UserWithAuxData extends User
{
	/**
	 * The email address for the user. Not used as a key, only provided as additional information.
	 * Always stored in lowercase.
	 */
	@simpl_scalar
	private String	auxUserData	= "";

	private int			teamSpec		= -1;

	/** No-argument constructor for serialization. */
	public UserWithAuxData()
	{
		super();
	}

	/**
	 * Creates a new AuthenticationListEntry with the given username and password.
	 * 
	 * @param username
	 *          - the name of the user.
	 * @param plaintextPassword
	 *          - the password; will be hashed before it is stored.
	 */
	public UserWithAuxData(String userKey, String plaintextPassword, String auxUserData)
	{
		super(userKey, plaintextPassword);

		this.setAuxUserData(auxUserData);
	}

	/**
	 * @see ecologylab.generic.Debug#toString()
	 */
	@Override
	public String toString()
	{
		return super.toString() + " (" + auxUserData + ")";
	}

	/**
	 * @return the auxUserData
	 */
	public String getAuxUserData()
	{
		return auxUserData;
	}

	/**
	 * @param auxUserData
	 *          the auxUserData to set
	 */
	public void setAuxUserData(String auxUserData)
	{
		if (auxUserData != null)
			this.auxUserData = auxUserData;
	}

	/**
	 * @return the teamSpec
	 */
	public int getTeamSpec()
	{
		return teamSpec;
	}

	/**
	 * @param teamSpec the teamSpec to set
	 */
	public void setTeamSpec(int teamSpec)
	{
		this.teamSpec = teamSpec;
	}
}
