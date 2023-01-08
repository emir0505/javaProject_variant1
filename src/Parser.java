import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Parser {
    public static ArrayList<String[]> parse(String path) throws IOException, CsvValidationException {
        var rows = new ArrayList<String[]>();
        var reader = new CSVReader(new BufferedReader(new FileReader(path)));
        reader.readNext();

        String[] line;
        while ((line = reader.readNext()) != null)
            rows.add(line);

        return rows;
    }
}