
package nio.base;

/**
 * Interface pre objekt, ktory bude uchovavat informacie o pozicii v subore.
 * 
 * @author Lukas Sekerak
 */
public interface IPosInfo
{
	public long getPos();
	public int getSize();
	public void setPos(long a);
	public void setSize(int b);
}
