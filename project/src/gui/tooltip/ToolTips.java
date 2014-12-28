
package gui.tooltip;

import java.util.EmptyStackException;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 * Trieda riadi zobrazenia a pridavanie tooltipov v programe.
 * Tooltipi postupne zobrazuju stav programu.
 * 
 * @author Lukas Sekerak 
 */
public class ToolTips extends VerticalAnimatedComponent implements IStatus
{
	protected LinkedList<String>	messages;
	private int						timeWait;
	private int						stav;
	private final static Logger		logger	= Logger.getLogger(ToolTips.class.getName());

	public ToolTips() {
		super(40);
		stav = -1;
		messages = new LinkedList<String>();
		// setStatus("aaaaa");
		// setStatus("bbbBBbbb");
		// setStatus("adsadsa adsac");
		@SuppressWarnings("serial")
		JLabelCustom comp = new JLabelCustom()
		{
			/**
			 * Otazka je ako determinovat ze ide o systemove a uzivatelske volanie tejto funkcie.
			 * 
			 * Systemove musim zistit aby sme na zaciatku vedeli poziciu.
			 * Potom ho blokujeme ked nesedi animacna pozicia s tou ktoru nastavujeme.
			 */
			public void setBounds(int x, int y, int width, int height) {
				// System.out.println("setBounds");
				if (isFixedPos()) {
					super.setBounds(x, y, width, height);
					return;
				}
				super.setBounds(x, tempPos.y, width, height);
			}
			public void setLocation(int x, int y) {
				super.setLocation(getLocation().x, y);
			}
		};
		setComponent(comp);
		start();
		logger.info("initialized");
	}
	
	@Override
	public void setStatus(String status, int stav) {
		logger.info(status);
		this.stav = stav;
		messages.add(status);
	}
	
	protected void animationStart() {
		timeWait = 20;
		super.animationStart();

		try {
			String nextStatus = messages.poll();
			JLabelCustom label = ((JLabelCustom) component);
			// Opravuje artefakt - nasledne dalsia pozicia je uz fixnuta na nasu
			label.setFixedPos(false);
			label.setText(nextStatus);
			// label.repaint();
		}
		catch (EmptyStackException e) {
			// Ponechaj aktualny status a nerob nic...
		}

	}

	protected void animation() {
		if (tempPos.y == getPovodnePos().y) {
			// Stred animacie, pockaj casovy moment
			// TODO: Threed sleep je tu lepsie
			timeWait--;
			if (timeWait > 0) return;
			// Animacia nepokracuje dalej kedze nemame dalsie spravy
			if (messages.isEmpty()) return;
		}
		super.animation();
	}
	@Override
	public int getStatus() {
		return stav;
	}
}
