
package gui.menu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * JCheckBoxMenuItem, ktory sa nezatvara.
 * 
 * @author Darryl http://tips4java.wordpress.com/2010/09/12/keeping-menus-open/
 * @licence Darryl: "Like all code published on the blog, these classes are free to use, at your own risk . A link to the blog page in the doc comments or elsewhere would be nice, but is by no means mandatory."
 */
public class JCheckBoxMenuItemDontClose extends JCheckBoxMenuItem
{
	private static final long		serialVersionUID	= -7968158890430613274L;
	
	private static MenuElement[]	path;
	{
		getModel().addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(ChangeEvent e) {
				if (getModel().isArmed() && isShowing()) {
					path = MenuSelectionManager.defaultManager().getSelectedPath();
				}
			}
		});
	}

	public void doClick(int pressTime) {
		super.doClick(pressTime);
		MenuSelectionManager.defaultManager().setSelectedPath(path);
	}
	
	public JCheckBoxMenuItemDontClose() {
		super();
	}
}
