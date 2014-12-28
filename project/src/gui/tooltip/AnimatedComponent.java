
package gui.tooltip;

import java.awt.Point;
import javax.swing.JComponent;

/**
 * Trieda ktora vytvori Component, ktory moze byt animovany.
 * Tzv sa pohybuje urcitym smerom.
 * 
 * @author Lukas Sekerak
 */
public abstract class AnimatedComponent
{
	protected JComponent	component;
	protected Point			tempPos;
	private Point			povodnePos;
	private boolean			umiestneny;
	protected Thread		thread;

	public AnimatedComponent() {
		component = null;
		umiestneny = false;
	}

	/**
	 * Spusti beh animacie.
	 */
	public void start() {
		Runnable akcia = new Runnable()
		{
			public void run() {
				try {
					while (true) {
						tickrate();
						Thread.sleep(60);
					}
				}
				catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		};

		thread = new Thread(akcia, "Tooltip spravca");
		Thread.UncaughtExceptionHandler exh = new Thread.UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread th, Throwable ex) {
				ex.printStackTrace();
			}
		};
		thread.setUncaughtExceptionHandler(exh);
		thread.start();
	}

	private void tickrate() {
		if (umiestneny == false) {
			// Component nieje na svojom mieste,... cakame na
			// umiestnenie vsetkych komponentov pomocou pack
			Point pos = component.getLocation();
			if (pos.x == 0 && pos.y == 0) return;

			// Komponent je umiestneny
			umiestneny = true;
			povodnePos = pos.getLocation();
			animationStart();
		}
		animation();
	}

	public Point getPovodnePos() {
		return povodnePos;
	}

	protected abstract void animationStart();
	protected abstract void animationEnd();
	protected abstract void animation();

	/**
	 * je lement na pociatocnej pozicii ?
	 * 
	 * @return
	 */
	public boolean isUmiestneny() {
		return umiestneny;
	}

	protected void resetLocation() {
		if (umiestneny) setLocation(povodnePos);
	}

	public JComponent getComponent() {
		return component;
	}

	protected void setComponent(JComponent component) {
		this.component = component;
	}

	protected void setLocation(Point pos) {
		component.setLocation(pos);
	}
}
