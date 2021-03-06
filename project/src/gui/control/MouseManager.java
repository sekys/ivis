
package gui.control;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.plaf.basic.BasicIconFactory;
import org.apache.log4j.Logger;
import render.DisplayControl;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.LabelEditingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import graph.GraphHolder;
import graph.objects.Vertex;
import graph.util.TranslatingSreen;
import gui.tooltip.IStatus;

/**
 * Trieda spravuje vsetky akcie vytvorene mysou.
 * To znamena aj pohyb po pracovnej ploche.
 * 
 * @autor EditingModalGraphMouse
 * @author Lukas Sekerak - edited
 */
public class MouseManager extends AbstractModalGraphMouse
{
	protected EditingGraphMousePlugin<Vertex, Integer>		editingPlugin;
	protected LabelEditingGraphMousePlugin<Vertex, Integer>	labelEditingPlugin;
	protected PopupPlugin									popupEditingPlugin;
	protected AnnotatingGraphMousePlugin<Vertex, Integer>	annotatingPlugin;

	private final static Logger								logger	= Logger.getLogger(MouseManager.class.getName());

	public MouseManager(VisualizationViewer<Vertex, Integer> vv,
			GraphHolder holder,
			IStatus status,
			DisplayControl dp,
			TranslatingSreen ts) {
		super(1.1f, 1 / 1.1f);
		logger.info("starting");

		pickingPlugin = new PickingPlugin(holder.getQuad());
		animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<Vertex, Integer>();
		translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
		scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
		rotatingPlugin = new RotatingGraphMousePlugin();
		shearingPlugin = new ShearingGraphMousePlugin();
		editingPlugin = null;
		labelEditingPlugin = new LabelEditingGraphMousePlugin<Vertex, Integer>();
		annotatingPlugin = new AnnotatingGraphMousePlugin<Vertex, Integer>(vv.getRenderContext());
		popupEditingPlugin = new PopupPlugin(vv, holder, status, dp, ts);
		logger.info("all plugins created");

		loadPlugins();
		setModeKeyListener(new ModeKeyAdapter(this));
	}

	protected void loadPlugins() {
		add(scalingPlugin);
		setMode(Mode.TRANSFORMING);
	}

	@Override
	public void setMode(Mode mode) {
		if (this.mode != mode) {
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this.mode, ItemEvent.DESELECTED));
			this.mode = mode;
			if (mode == Mode.TRANSFORMING) {
				setTransformingMode();
			} else if (mode == Mode.PICKING) {
				setPickingMode();
			} else if (mode == Mode.ANNOTATING) {
				setAnnotatingMode();
			}
			if (modeBox != null) {
				modeBox.setSelectedItem(mode);
			}
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
		}
	}

	@SuppressWarnings("serial")
	public JComboBox getModeComboBox() {
		if (modeBox == null) {
			modeBox = new JComboBox(new Mode[]{Mode.TRANSFORMING, Mode.PICKING,
					Mode.ANNOTATING});
			modeBox.setRenderer(new DefaultListCellRenderer()
			{
				@Override
				public Component getListCellRendererComponent(final JList list,
						Object value, final int index,
						final boolean isSelected, final boolean cellHasFocus) {

					Mode mod = (Mode) value;
					if (mod == Mode.TRANSFORMING) {
						value = "Pohyb a rot�cia";
					} else if (mod == Mode.ANNOTATING) {
						value = "Pozn�mkov� m�d";
					} else if (mod == Mode.PICKING) {
						value = "Ozna�ovanie";
					}
					return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				}
			});
			modeBox.addItemListener(getModeListener());
		}
		modeBox.setSelectedItem(mode);
		return modeBox;
	}

	@Override
	protected void setPickingMode() {
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(annotatingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	@Override
	protected void setTransformingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(annotatingPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	protected void setAnnotatingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(labelEditingPlugin);
		remove(popupEditingPlugin);
		add(annotatingPlugin);
	}
	public JMenu getModeMenu() {
		if (modeMenu == null) {
			modeMenu = new JMenu();// {
			Icon icon = BasicIconFactory.getMenuArrowIcon();
			modeMenu.setIcon(BasicIconFactory.getMenuArrowIcon());
			modeMenu.setPreferredSize(new Dimension(icon.getIconWidth() + 10, icon.getIconHeight() + 10));

			final JRadioButtonMenuItem transformingButton = new JRadioButtonMenuItem("Pohyb po obrazovke");
			transformingButton.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.TRANSFORMING);
					}
				}
			});

			final JRadioButtonMenuItem pickingButton = new JRadioButtonMenuItem("Oznacovanie");
			pickingButton.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.PICKING);
					}
				}
			});

			ButtonGroup radio = new ButtonGroup();
			radio.add(transformingButton);
			radio.add(pickingButton);
			transformingButton.setSelected(true);
			modeMenu.add(transformingButton);
			modeMenu.add(pickingButton);
			modeMenu.setToolTipText("Vo�ba ovl�dania my�i.");
			addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (e.getItem() == Mode.TRANSFORMING) {
							transformingButton.setSelected(true);
						} else if (e.getItem() == Mode.PICKING) {
							pickingButton.setSelected(true);
						}
					}
				}
			});
		}
		return modeMenu;
	}

	public static class ModeKeyAdapter extends KeyAdapter
	{
		private char				t	= 't';
		private char				p	= 'r';
		private char				a	= 'z';
		protected ModalGraphMouse	graphMouse;

		public ModeKeyAdapter(ModalGraphMouse graphMouse) {
			this.graphMouse = graphMouse;
		}

		public ModeKeyAdapter(char t, char p, char a, ModalGraphMouse graphMouse) {
			this.t = t;
			this.p = p;
			this.a = a;
			this.graphMouse = graphMouse;
		}

		@Override
		public void keyTyped(KeyEvent event) {
			char keyChar = event.getKeyChar();
			if (keyChar == t) {
				((Component) event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				graphMouse.setMode(Mode.TRANSFORMING);
			} else if (keyChar == p) {
				((Component) event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				graphMouse.setMode(Mode.PICKING);
			} else if (keyChar == a) {
				((Component) event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				graphMouse.setMode(Mode.ANNOTATING);
			}
		}
	}
}
