
package gui.tooltip;

/**
 * AnimatedComponent ktory sa pohybuje vo vertikalnom smere.
 * 
 * @author Lukas Sekerak
 */
public class VerticalAnimatedComponent extends AnimatedComponent
{
	protected int	actualY;
	protected int	animationY;

	public VerticalAnimatedComponent(int y) {
		animationY = y;
	}
	protected void animationStart() {
		actualY = animationY;
		tempPos = getPovodnePos().getLocation();
		tempPos.y += actualY / 2;
		actualY += actualY / 2;
		setLocation(tempPos);
	}
	protected void animationEnd() {
		animationStart();
	}
	protected void animation() {
		if (actualY == 0) {
			animationEnd();
			return;
		}
		actualY--;
		tempPos.y--;
		setLocation(tempPos);
		component.repaint();
	}
}
