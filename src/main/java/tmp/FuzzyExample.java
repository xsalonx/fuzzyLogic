package tmp;

import chart.ChartDisplayer;
import chart.DataSetBuilder;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.LinkedList;

public class FuzzyExample {

    enum FuzzyTerms{

    }


    public static void main(String[] args) {

        System.out.println(Math.toRadians(-10));
        String fileName = "example.fcl";
        int intensityLevel = 0;
        FIS fis = FIS.load(fileName, false);
        FuzzyRuleSet fuzzyRuleSet = fis.getFuzzyRuleSet();
        fuzzyRuleSet.setVariable("poziom_natezenia", intensityLevel);

        LinkedList<Double> Y = new LinkedList<>();
        LinkedList<String> X = new LinkedList<>();
        String seriesName = "poziom natezenia";

        double change, prevValue, nextValue, dayPart;
        for (int d = 0; d < 5; d++) {
            for (int dayHour = 0; dayHour < 23; dayHour++) {
                for (int minute = 0; minute < 60; minute += 1) {
                    dayPart = (double) dayHour + (double) minute / 60;
                    fuzzyRuleSet.setVariable("pora_dnia", dayPart);
                    fuzzyRuleSet.evaluate();

                    change = fuzzyRuleSet.getVariable("zmiana_natezenia").getLatestDefuzzifiedValue();
                    prevValue = fuzzyRuleSet.getVariable("poziom_natezenia").getValue();
                    nextValue = prevValue + change;
                    fuzzyRuleSet.setVariable("poziom_natezenia", nextValue);

                    Y.add(nextValue);
                    X.add(String.valueOf(dayPart + d * 24));
                }
            }
            System.out.println(d);
        }

        DefaultCategoryDataset dataset = (new DataSetBuilder()).addSeries(seriesName, Y, X).getDataset();
        ChartDisplayer chartDisplayer = new ChartDisplayer("", "", "", "", dataset);
        chartDisplayer.display();
    }

}