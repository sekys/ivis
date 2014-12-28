
package gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import render.DisplayControl;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.NumberFormattingTransformer;
import graph.objects.Vertex;

/**
 * EdgeMenu vytvara menu v hornej casti obrazovky.
 * Obsahuje vsetky nastavenia an zobrazovanie hran.
 * 
 * @author Lukas Sekerak
 */
public class EdgeMenu extends JMenu implements ActionListener
{
	private static final long						serialVersionUID	= -7383592867553809123L;

	protected JCheckBoxMenuItem						allowPicked;
	protected JCheckBoxMenuItem						showVolume;
	protected JCheckBoxMenuItem						allowRender;
	protected JSlider								VolumeFilter;

	protected VisualizationViewer<Vertex, Integer>	vv;
	protected DisplayControl						dp;
	protected Transformer<Integer, Float>			voltages;

	public EdgeMenu(VisualizationViewer<Vertex, Integer> vv,
			Transformer<Integer, Float> voltages,
			DisplayControl dp) {

		super("Hrany");
		this.vv = vv;
		this.voltages = voltages;
		this.dp = dp;
		this.setToolTipText("Menu nastavenÌ pre hrany.");

		allowRender = new JCheckBoxMenuItem("Vykreslovaù hrany");
		allowRender.addActionListener(this);
		add(allowRender);

		allowPicked = new JCheckBoxMenuItem("Zobrazovaù hrany len pre vybranÈ vrcholi");
		allowPicked.addActionListener(this);
		add(allowPicked);

		JSlider volumeSlider;
		volumeSlider = new JSlider(0, 100, 0);
		volumeSlider.setToolTipText("Filter podæa v·hy hr·n.");
		volumeSlider.addChangeListener(new VolumeSlider());
		add(volumeSlider);

		showVolume = new JCheckBoxMenuItem("Zobrazovaù v·hu hr·n");
		showVolume.addActionListener(this);
		add(showVolume);

		// Dopasuj display predicate este
		allowRender.setSelected(true);
		allowPicked.setSelected(false);
		showVolume.setSelected(true);

		dp.setEdgeMinVolume(0.0f);
		dp.setEdgeOnlyPicked(false);
		dp.setEdgeRenderAllow(true);
	}

	private class VolumeSlider implements ChangeListener
	{
		public void stateChanged(ChangeEvent changeEvent) {
			JSlider source = (JSlider) changeEvent.getSource();
			if (!source.getValueIsAdjusting()) {
				dp.setEdgeMinVolume((float) source.getValue());
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void actionPerformed(ActionEvent e) {
		AbstractButton source = (AbstractButton) e.getSource();

		if (source == allowPicked) {
			dp.setEdgeOnlyPicked(source.isSelected());
		} else if (source == showVolume) {
			if (source.isSelected()) {
				vv.getRenderContext().setEdgeLabelTransformer(new NumberFormattingTransformer<Integer>(voltages));
			} else {
				vv.getRenderContext().setEdgeLabelTransformer(new ConstantTransformer(null));
			}
		} else if (source == allowRender) {
			dp.setEdgeRenderAllow(source.isSelected());
		}

		vv.repaint();
	}
}
