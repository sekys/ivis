
package app;

/**
 * Hlavna trieda, spusti sa aplikacia.
 * 
 * @author Lukas Sekerak
 */
public class App extends VisualizerScreen
{
	private App() {
		super();
	}

	private static class AppHolder
	{
		public static final App	INSTANCE	= new App();
	}

	public static App getInstance() {
		return AppHolder.INSTANCE;
	}
	public static void main(String[] args) {
		App.getInstance();
	}
	
	/**
	 * Metoda je zavolana z GUI
	 */
	public void Zatvorit() {
		System.exit(0);
	}
}
