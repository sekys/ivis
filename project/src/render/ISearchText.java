
package render;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Interface pre nastavenie vyhladavaneho textu
 * 
 * @author Lukas Sekerak
 */
public interface ISearchText
{
	public void setHladanyretazec(Pattern ret) throws PatternSyntaxException;
}
