package chart;

import org.jfree.data.category.DefaultCategoryDataset;

import java.util.Iterator;

public class DataSetBuilder {

    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    public DataSetBuilder() {}
    public DataSetBuilder addSeries(String seriesName, Iterable<? extends Number> Y, Iterable<String> X) {
        Iterator<? extends Number> itY = Y.iterator();
        Iterator<String> itX = X.iterator();
        while (itX.hasNext() && itY.hasNext()) {
            dataset.addValue((Number) itY.next(), seriesName, itX.next());
        }
        return this;
    }

    public void clear() {
        dataset = new DefaultCategoryDataset();
    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

//    public DefaultCategoryDataset getWithCopy() {
//        p
//        return dataset;
//    }
}
