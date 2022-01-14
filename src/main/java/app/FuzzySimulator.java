package app;

import lombok.Getter;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

@Getter
public class FuzzySimulator {
    public enum FuzzyTerms{
        border_distance,
        collision_distance,
        collision_angle,
        velocity,
        direct_angle,
        direct_change,
        velocity_change;

        public final String s;
        FuzzyTerms() {
            s = this.toString();
        }

    }
    private final String fclFile = "controller.fcl";

    private FuzzyRuleSet fuzzyRuleSet;
    private ExecutionController executionController;
    private int width;
    private int height;
    private int currentY;
    private int currentX;
    private double currentVelocity;
    private double currentDirect;
    private double collisionAngle;
    private double collisionDistance;
    private final double stepRate = 1;

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
        trapX = width/2;
        trapY = 0;
        trapRadius = (int) (Math.min(width, height) * trapRadiusRate);

        FIS fis = FIS.load(fclFile, false);

        fuzzyRuleSet = fis.getFuzzyRuleSet();
        currentDirect = 0;
        fuzzyRuleSet.setVariable(FuzzyTerms.direct_angle.s, currentDirect);
        currentVelocity = 1;
        fuzzyRuleSet.setVariable(FuzzyTerms.velocity.s, currentVelocity);
//        fuzzyRuleSet.chart();
    }

    public void run() {
        setFuzzyVariables();
        while (!executionController.checkIfEnd()){
            System.out.println(currentVelocity + " " + currentX + " " + currentY + " " + trapX + " " + trapY);
            System.out.println(currentDirect + " " + collisionAngle + " " + collisionDistance + '\n');
            move();

            setFuzzyVariables();
            fuzzyRuleSet.evaluate();
            updateData();
            executionController.waitIfEnabled();
            executionController.sleepSimulation();
        }

    }

    private void move() {
        currentX += 5 * stepRate * Math.sin(Math.toRadians(currentDirect)) * currentVelocity;
        trapY += stepRate * Math.cos(Math.toRadians(currentDirect)) * currentVelocity;
        if (trapY > currentY + (height - currentY) / 2)
            createNewTrap();
    }
    private void setFuzzyVariables() {
        double d;
        if (trapY < currentY) {
            if (Math.abs(currentDirect) > 1) {
                double A = -1 / Math.tan(Math.toRadians(currentDirect));
                double C = currentY - A * currentX;
                d = spaceScaler * Math.abs(A * trapX + trapY + C) / Math.sqrt(A * A + 1);
            } else {
                d = Math.abs(currentX - trapX);
            }
            if (d < trapRadius) {
                collisionAngle = 90 - Math.toDegrees(Math.acos(d / trapRadius));
            } else {
                collisionAngle = -10;
            }
        } else {
            collisionAngle = -10;
        }
        fuzzyRuleSet.setVariable(FuzzyTerms.collision_angle.s, collisionAngle);

        fuzzyRuleSet.setVariable(FuzzyTerms.border_distance.s, Math.min(currentX, width - currentX)*0.1);

        collisionDistance = 0.1 * Math.sqrt(Math.pow(currentX - trapX, 2) + Math.pow(currentY - trapY, 2));
        fuzzyRuleSet.setVariable(FuzzyTerms.collision_distance.s, collisionDistance);



    }
    private void updateData() {
        currentVelocity += fuzzyRuleSet.getVariable(FuzzyTerms.velocity_change.s).getLatestDefuzzifiedValue();
        fuzzyRuleSet.setVariable(FuzzyTerms.velocity.s, currentVelocity);

        currentDirect += fuzzyRuleSet.getVariable(FuzzyTerms.direct_change.s).getLatestDefuzzifiedValue();
        fuzzyRuleSet.setVariable(FuzzyTerms.direct_angle.s, currentDirect);
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
