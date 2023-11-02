package it.izs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvUtils {;

	public static void creaCsvOutput(String contenutoFileCsv, String pathCsvFileOutput) {
		try {
			// Apriamo il file in scrittura
			PrintStream ps = new PrintStream(new FileOutputStream(pathCsvFileOutput));

			ps.println(contenutoFileCsv);

			// Chiudiamo il file
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
//	public static List<Object> csvToList(File input) throws Exception {
//		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
//		CsvMapper csvMapper = new CsvMapper();
//
//		// Read data from CSV file
//		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();
//
//		return readAll;
//	}
	
	public static List<String> csvToList(File input) throws Exception {
		List<String> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        String singleValue = line;
		        records.add(singleValue);
		    }
		}

		return records;
	}
	
	
	public static String csvToJson(String csvFile) throws Exception {
		File input = new File(csvFile);

		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();

		ObjectMapper mapper = new ObjectMapper();

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);

		return jsonString;

	}
	
	public static String csvToJson(File input) throws Exception {
		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();

		ObjectMapper mapper = new ObjectMapper();

		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);

		return jsonString;

	}

}
