import com.opencsv.exceptions.CsvValidationException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws CsvValidationException, SQLException, IOException, ClassNotFoundException {
        var db = new Database("country.sqlite", null);
        // 1
        var r1 = db.doQuery("SELECT name, economy FROM country");
        var dataset = new DefaultCategoryDataset();
        while (r1.next()) {
            var country = r1.getString("name");
            var economy = r1.getDouble("economy");
            dataset.addValue(economy, "Экономика", country);
        }
        var chart = ChartFactory.createBarChart("График", "Страна", "Экономика", dataset);
        chart.removeLegend();
        chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        var frame = new JFrame("1:");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
        r1.close();
        // 2
        var r2 = db.doQuery("""
                SELECT name, MAX(economy)
                FROM country
                WHERE region IN ('Latin America and Caribbean', 'Eastern Asia')
                """);
        System.out.println("2: " + r2.getString("name"));
        r2.close();
        // 3
        var r3 = db.doQuery("""
                SELECT name, (happiness_score +
                    standard_error +
                    economy +
                    family +
                    health +
                    freedom +
                    trust +
                    generosity +
                    dystopia_residual) / 9 AS score
                FROM country
                WHERE region IN ('Western Europe', 'North America')
                ORDER BY score DESC
                """);
        var avgCountries = new ArrayList<String>();
        while (r3.next())
            avgCountries.add(r3.getString("name"));
        System.out.println("3: " + avgCountries.get(avgCountries.size() / 2));
        r3.close();
    }
}