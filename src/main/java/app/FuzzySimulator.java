package app;

import lombok.Getter;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

@Getter
public class FuzzySimulator {
    public enum FuzzyTerms{
        left_border_distance,
        right_border_distance,
        trap_distance,
        direct_angle,
        direct_change;

        public final String s;
        FuzzyTerms() {
            s = this.toString();
        }

    }
    private final String fclFile = "controller.fcl";

    private final FuzzyRuleSet fuzzyRuleSet;
    private final ExecutionController executionController;
    private final int width;
    private final int height;
    private int currentY;
    private int currentX;
    private final double velocity;
    private double currentDirect;
    private double trapDistance;
    private final double stepRate = 1;

    private int trapX;
    private int trapY;
    private final int trapRadius;
    private final static double trapRadiusRate = 0.1;
    private final static double spaceScaler = 1;

    public FuzzySimulator(ExecutionController executionController) {
        this.executionController = executionController;
        this.width = 500;
        this.height = 500;
        currentX = width / 2;
        currentY = (int) (height * 0.8);
        trapX = width / 2;
        trapY = 0;
        trapRadius = (int) (Math.min(width, height) * trapRadiusRate);

        FIS fis = FIS.load(fclFile, false);

        fuzzyRuleSet = fis.getFuzzyRuleSet();
        currentDirect = 0;
        fuzzyRuleSet.setVariable(FuzzyTerms.direct_angle.s, currentDirect);
        velocity = 2;
//        fuzzyRuleSet.chart();
    }

    public void run() {
        while (!executionController.checkIfEnd()){
            setFuzzyInputs();

            System.out.println(currentX + " " + currentY + " " + trapX + " " + trapY);
            System.out.println(currentDirect + " " + " " + trapDistance + " " + '\n');
            fuzzyRuleSet.evaluate();
            updateFromFuzzyOutputs();
            move();

            executionController.waitIfEnabled();
            executionController.sleepSimulation();
        }

    }

    private void move() {
        double sin=Math.sin(Math.toRadians(currentDirect));
        double cos=Math.cos(Math.toRadians(currentDirect));
        currentX += 2 * stepRate * sin * velocity;
        if (currentX < 0.1 * width)
            currentX = (int)(0.1 * width);
        else if (currentX > 0.9 * width)
            currentX = (int)(0.9 * width);

        trapY += stepRate * cos * velocity;
        System.out.println("sincosing: "+ sin + ", " + cos);
    }
    private void setFuzzyInputs() {
        if (trapY > currentY + (height - currentY) / 2)
            createNewTrap();
        trapDistance = (currentX - trapX) * spaceScaler;

        System.out.println("borders: " + currentX + ", " + (width - currentX));
        fuzzyRuleSet.setVariable(FuzzyTerms.left_border_distance.s, currentX * spaceScaler);
        fuzzyRuleSet.setVariable(FuzzyTerms.right_border_distance.s, (width - currentX) * spaceScaler);

//        collisionDistance = 0.1 * Math.sqrt(Math.pow(currentX - trapX, 2) + Math.pow(currentY - trapY, 2));
        fuzzyRuleSet.setVariable(FuzzyTerms.trap_distance.s, trapDistance);
    }

    private void updateFromFuzzyOutputs() {
        double value = fuzzyRuleSet.getVariable(FuzzyTerms.direct_change.s).getLatestDefuzzifiedValue();
        System.out.println("direction changes: " + value);
        if (!Double.isNaN(value)) {
            currentDirect += value;
            if (currentDirect > 85)
                currentDirect = 86;
            else if (currentDirect < -85)
                currentDirect = -85;
            fuzzyRuleSet.setVariable(FuzzyTerms.direct_angle.s, currentDirect);
            System.err.println("value is NaN ;/");
        }
    }

    private void createNewTrap() {
        trapY = 5;
        trapX = (int) (Math.random() * width);
    }

    public static void main(String[] args) {
        FuzzySimulator fuzzySimulator = new FuzzySimulator(new ExecutionController());
        fuzzySimulator.run();
    }

}
