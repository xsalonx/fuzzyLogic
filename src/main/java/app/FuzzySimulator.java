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
    private double collisionDistance;
    private final double stepRate = 0.5;

    private int trapX;
    private int trapY;
    private final int trapRadius;
    private final static double trapRadiusRate = 0.1;
    private final static double spaceScaler = 0.1;

    public FuzzySimulator(ExecutionController executionController, int width, int height) {
        this.executionController = executionController;
        this.width = width;
        this.height = height;
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
        setFuzzyInputs();
        while (!executionController.checkIfEnd()){
            System.out.println(currentX + " " + currentY + " " + trapX + " " + trapY);
            System.out.println(currentDirect + " " + " " + collisionDistance + " " + '\n');
            move();

            setFuzzyInputs();
            fuzzyRuleSet.evaluate();
            updateFromFuzzyOutputs();
            executionController.waitIfEnabled();
            executionController.sleepSimulation();
        }

    }

    private void move() {
        currentX += 3 * stepRate * Math.sin(Math.toRadians(currentDirect)) * velocity;
        trapY += stepRate * Math.cos(Math.toRadians(currentDirect)) * velocity;
        if (trapY > currentY + (height - currentY) / 2)
            createNewTrap();
    }
    private void setFuzzyInputs() {
        if (trapY >= currentY) {
            createNewTrap();
        }
        collisionDistance = (currentX - trapX) * 0.1;

        System.out.println("borders: " + currentX + ", " + (width - currentX));
        fuzzyRuleSet.setVariable(FuzzyTerms.left_border_distance.s, currentX * 0.1);
        fuzzyRuleSet.setVariable(FuzzyTerms.right_border_distance.s, (width - currentX)*0.1);

//        collisionDistance = 0.1 * Math.sqrt(Math.pow(currentX - trapX, 2) + Math.pow(currentY - trapY, 2));
        fuzzyRuleSet.setVariable(FuzzyTerms.trap_distance.s, collisionDistance);
    }

    private void updateFromFuzzyOutputs() {
        Double value = fuzzyRuleSet.getVariable(FuzzyTerms.direct_change.s).getLatestDefuzzifiedValue();
        System.out.println("direction changes: " + value);
        if (!value.isNaN()) {
            currentDirect += value;
            fuzzyRuleSet.setVariable(FuzzyTerms.direct_angle.s, currentDirect);
            System.err.println("value is NaN ;/");
        }
    }

    private void createNewTrap() {
        trapY = 5;
        double d = Math.min(currentX, width - currentX);
        trapX = (int) ((Math.random() - 0.5) / 2 * d + currentX);
    }

    public static void main(String[] args) {
        FuzzySimulator fuzzySimulator = new FuzzySimulator(new ExecutionController(), 500, 500);
        fuzzySimulator.run();
    }

}
