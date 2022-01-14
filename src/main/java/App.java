import app.ExecutionController;
import app.FuzzySimulator;
import app.gui.Gui;

public class App {

    private final ExecutionController executionController;
    private final Gui gui;

    private App() {
        executionController = new ExecutionController();
        gui = new Gui(executionController);
    }
    private void start() {
        gui.start();
    }

    public static void main(String[] args) {
        App app = new App();
        app.start();
    }
}
