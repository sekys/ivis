
package gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections15.Transformer;
import render.DisplayControl;
import render.VertexStrokeHighlight;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.NumberFormattingTransformer;
import graph.GraphHolder;
import graph.objects.Vertex;
import graph.objects.VertexGroup;
import graph.util.VertexShapeSizeAspect;

/**
 * VerticesMenu vytvara menu v hornej casti obrazovky.
 * Obsahuje vsetky nastavenia na zobrazenie Vrcholov.
 * 
 * @author Lukas Sekerak
 */
@SuppressWarnings("serial")
public class VerticesMenu extends JMenu implements ActionListener
{

	protected JCheckBoxMenuItem							hrubkaZvyraznenie;
	protected JCheckBoxMenuItem							rozneTvary;
	protected JCheckBoxMenuItem							velkostPodlaVolume;
	protected JRadioButtonMenuItem						labelSpolu;
	protected JRadioButtonMenuItem						labelKeyValuePriority;
	protected JRadioButtonMenuItem						labelKeyValue;
	protected JRadioButtonMenuItem						labelValue;
	protected JRadioButtonMenuItem						labelVolume;
	protected JRadioButtonMenuItem						labelNo;
	protected JCheckBoxMenuItem							autoZoomVolumeFilter;
	protected JSlider									VolumeFilter;
	protected JMenuItem									resetVisibility;

	protected VertexStrokeHighlight<Vertex, Integer>	strokeChanger;
	protected VertexShapeSizeAspect<Vertex, Integer>	shapeChanger;
	protected VisualizationViewer<Vertex, Integer>		vv;
	protected DisplayControl							dp;
	protected Transformer<Vertex, Float>				voltages;
	protected GraphHolder								holder;

	public VerticesMenu(VisualizationViewer<Vertex, Integer> vv,
			Graph<Vertex, Integer> g,
			Transformer<Vertex, Float> voltages,
			GraphHolder holder,
			DisplayControl dp) {
		super("Vrcholy");
		this.vv = vv;
		this.voltages = voltages;
		this.holder = holder;
		this.dp = dp;
		this.setToolTipText("Menu nastavenÌ pre hrany.");

		// CTRL + L
		// leftJustify.setAccelerator(KeyStroke.getKeyStroke('L',
		// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		JSlider volumeSlider = new JSlider(0, 100, 0);
		volumeSlider.setToolTipText("Filter podæa ohodnotenia vrcholov.");
		// volumeSlider.setPaintTicks(true);
		// volumeSlider.setMajorTickSpacing(20);
		volumeSlider.addChangeListener(new VolumeSlider());
		add(volumeSlider);

		JSlider degreeSlider = new JSlider(0, 25, 1);
		degreeSlider.setToolTipText("Filter podæa poËtu susedov.");
		degreeSlider.addChangeListener(new DegreeSlider());
		add(degreeSlider);

		// Radio
		LabelsRadioGroup();

		// Control
		autoZoomVolumeFilter = new JCheckBoxMenuItem("Automatick˝ filter podæa priblÌûenia");
		autoZoomVolumeFilter.addActionListener(this);
		add(autoZoomVolumeFilter);

		hrubkaZvyraznenie = new JCheckBoxMenuItem("Zv˝razni hruböie susedov");
		hrubkaZvyraznenie.addActionListener(this);
		add(hrubkaZvyraznenie);

		rozneTvary = new JCheckBoxMenuItem("Tvar podæa uhlu");
		rozneTvary.addActionListener(this);
		add(rozneTvary);

		velkostPodlaVolume = new JCheckBoxMenuItem("Veækost podæa ohodnotenia");
		velkostPodlaVolume.addActionListener(this);
		add(velkostPodlaVolume);

		JSlider tresholdSlider = new JSlider(1, 10, 6);
		tresholdSlider.setToolTipText("Hodnota treshold.");
		tresholdSlider.addChangeListener(new TresholdSlider());
		add(tresholdSlider);

		resetVisibility = new JMenuItem("Reötartuj viditeænosù");
		resetVisibility.addActionListener(this);
		add(resetVisibility);

		// Transformer stroke
		strokeChanger = new VertexStrokeHighlight<Vertex, Integer>(g, vv.getPickedVertexState());
		vv.getRenderContext().setVertexStrokeTransformer(strokeChanger);

		// Transformer size changer
		shapeChanger = new VertexShapeSizeAspect<Vertex, Integer>(g, voltages);
		vv.getRenderContext().setVertexShapeTransformer(shapeChanger);

		// Dopasuj display predicate este
		rozneTvary.setSelected(true);
		hrubkaZvyraznenie.setSelected(true);
		strokeChanger.setHighlight(true);
		shapeChanger.useFunnyShapes(true);
		autoZoomVolumeFilter.setSelected(false);
		velkostPodlaVolume.setSelected(true);
		shapeChanger.setScaling(true);

		dp.setMaxTreshold(6);
		dp.setManualMinVolume(0.0f / 20.f);
		dp.setMinSusedov(1);
		dp.setFilterByAutoZoomVolume(false);
	}

	private void LabelsRadioGroup() {
		ButtonGroup labels = new ButtonGroup();
		JMenu labelSubMenu = new JMenu("Form·t popisu");

		labelSpolu = new JRadioButtonMenuItem("Kæ˙Ë => Hodnota (Priorita) (Ohodnotenie)");
		labelSpolu.addActionListener(this);
		labels.add(labelSpolu);
		labelSubMenu.add(labelSpolu);

		labelKeyValuePriority = new JRadioButtonMenuItem("Kæ˙Ë => Hodnota (Priority)");
		labelKeyValuePriority.addActionListener(this);
		labels.add(labelKeyValuePriority);
		labelSubMenu.add(labelKeyValuePriority);

		labelKeyValue = new JRadioButtonMenuItem("Kæ˙Ë => Hodnota");
		labelKeyValue.addActionListener(this);
		labels.add(labelKeyValue);
		labelSubMenu.add(labelKeyValue);

		labelValue = new JRadioButtonMenuItem("Hodnota");
		labelValue.addActionListener(this);
		labels.add(labelValue);
		labelSubMenu.add(labelValue);

		labelVolume = new JRadioButtonMenuItem("Ohodnotenie");
		labelVolume.addActionListener(this);
		labels.add(labelVolume);
		labelSubMenu.add(labelVolume);

		labelNo = new JRadioButtonMenuItem("éiadny");
		labelNo.addActionListener(this);
		labels.add(labelNo);
		labelSubMenu.add(labelNo);

		labelKeyValue.setSelected(true);
		vv.getRenderContext().setVertexLabelTransformer(new Transformer<Vertex, String>()
		{
			@Override
			public String transform(Vertex v) {
				return v.getKeyValue();
			}
		});
		add(labelSubMenu);
	}

	/**
	 * Restartni vidtelnost prvkov
	 * TODO: moze sa lachko paralelizovat
	 */
	private void resetVerticesVisibility() {
		for (VertexGroup group : holder.getVertices().getGroups()) {
			group.resetVisibility();
		}
	}

	private class VolumeSlider implements ChangeListener
	{
		public void stateChanged(ChangeEvent changeEvent) {
			JSlider source = (JSlider) changeEvent.getSource();
			if (!source.getValueIsAdjusting()) {
				dp.setManualMinVolume((float) source.getValue() / 20.f);
			}
		}
	}

	private class DegreeSlider implements ChangeListener
	{
		public void stateChanged(ChangeEvent changeEvent) {
			JSlider source = (JSlider) changeEvent.getSource();
			if (!source.getValueIsAdjusting()) {
				dp.setMinSusedov(source.getValue());
			}
		}
	}

	private class TresholdSlider implements ChangeListener
	{
		public void stateChanged(ChangeEvent changeEvent) {
			JSlider source = (JSlider) changeEvent.getSource();
			if (!source.getValueIsAdjusting()) {
				dp.setMaxTreshold(source.getValue());
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		AbstractButton source = (AbstractButton) e.getSource();

		if (source == hrubkaZvyraznenie) {
			strokeChanger.setHighlight(source.isSelected());
		} else if (source == labelSpolu) {
			vv.getRenderContext().setVertexLabelTransformer(new Transformer<Vertex, String>()
			{
				@Override
				public String transform(Vertex v) {
					return v.getKeyValuePriority() + " "
							+ voltages.transform(v);
				}
			});
		} else if (source == labelKeyValuePriority) {
			vv.getRenderContext().setVertexLabelTransformer(new Transformer<Vertex, String>()
			{
				@Override
				public String transform(Vertex v) {
					return v.getKeyValuePriority();
				}
			});
		} else if (source == labelKeyValue) {
			vv.getRenderContext().setVertexLabelTransformer(new Transformer<Vertex, String>()
			{
				@Override
				public String transform(Vertex v) {
					return v.getKeyValue();
				}
			});
		} else if (source == labelValue) {
			vv.getRenderContext().setVertexLabelTransformer(new Transformer<Vertex, String>()
			{
				@Override
				public String transform(Vertex v) {
					return v.getValue();
				}
			});
		} else if (source == labelVolume) {
			vv.getRenderContext().setVertexLabelTransformer(new NumberFormattingTransformer<Vertex>(voltages));
		} else if (source == labelNo) {
			vv.getRenderContext().setVertexLabelTransformer(null);
		} else if (source == rozneTvary) {
			shapeChanger.useFunnyShapes(source.isSelected());
		} else if (source == velkostPodlaVolume) {
			shapeChanger.setScaling(source.isSelected());
		} else if (source == autoZoomVolumeFilter) {
			dp.setFilterByAutoZoomVolume(source.isSelected());
		} else if (source == resetVisibility) {
			resetVerticesVisibility();
		}

		vv.repaint();
	}
}
