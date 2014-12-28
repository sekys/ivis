package gui.tooltip;

import javax.swing.JLabel;

/**
 * Trieda opravuje arteakty pri animacii JLabelu.
 * 
 * @author Lukas Sekerak
 */
@SuppressWarnings("serial")
class JLabelCustom extends JLabel
{
	protected boolean fixedPosition = true;
	
	public JLabelCustom() {
		super(" ");
	}
	public boolean isFixedPos() {
		return fixedPosition;
	}
	public void setFixedPos(boolean canRepaint) {
		this.fixedPosition = canRepaint;
	}
}
