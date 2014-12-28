
package util;

/**
 * Vynimka pre nenajdeny objekt.
 * 
 * @author Lukas Sekerak
 */
public class NotFound extends Exception
{
	private static final long	serialVersionUID	= -2232422579792721907L;

	public NotFound() {}

	public NotFound(String msg) {
		super(msg);
	}
}
