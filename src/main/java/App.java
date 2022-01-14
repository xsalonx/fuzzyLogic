import app.ExecutionController;
import app.FuzzySimulator;
import app.gui.Gui;

public class App {

    private final ExecutionController executionController;
    private final FuzzySimulator fuzzySimulator;
    private final Gui gui;

    private App(String path) {
        executionController = new ExecutionController();
        fuzzySimulator = new FuzzySimulator(path, executionController);
        gui = new Gui(executionController, fuzzySimulator);
    }
    private void start() {
        gui.start();
    }
    public static void main(String[] args) {
        App app;
        if (args.length >= 1)
            app = new App(args[0]);
        else
            app = new App("./FCL/controller.fcl");
        app.start();
    }
}
