package it.izs.bioinfo.EfsaOhWgs;

import java.io.IOException;
import it.izs.api.Bioinfoquery;
import it.izs.api.general.GenericMain;
import it.izs.utils.JSONArray;
import it.izs.utils.JSONException;
import it.izs.utils.JSONObject;
import it.izs.utils.ShellUtils;
import it.izs.utils.Str;

public class EfsaCompatibility extends GenericMain {

	static EfsaOhWgsContext ctx = new EfsaOhWgsContext();
	static Bioinfoquery q;
	static ShellUtils sh = new ShellUtils();
	static String dirClass = ".";
	static JSONArray jaOut = new JSONArray();
	static String inputFilePath = "";
	static String inputFileName = "";
	static String outputDir = "";

	/******************************************
	 * MAIN WORKFLOW
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		init(args);
		_readSettings();
		JSONArray ja = ctx.jaListInputs;
		System.out.println("========================== INPUT: ==========================");
		System.out.println(ja.toString(3));
		System.out.println("========================== OUTPUT ==========================");

		ctx.jaListInputs = ja;
		for (int i = 0; i < ja.length(); i++) {
			ctx.joInput = ja.getJSONObject(i);
			CgMlstCompare.checkAllelicProfiles(
					ctx.joInput.getS("baseAllelicProfileFile"),
					ctx.joInput.getS("testAllelicProfileFile"));
			jaOut.put(ctx.cfg.getJSONObject("joAllelesCompareReport"));
			_setOutputDir(ctx.joInput.getS("cfg:outputDir"));
		}
		_writeOutput();
	}

	/**
	 * Assign the outputDir
	 * If more rows defines different cfg:outputDir, only the first one is set
	 */
	private static void _setOutputDir(String s) {
		if (!outputDir.equals("")) {
			return;
		}
		if (!Str.isDirectory(s)) {
			ctx.lg.error("_setOutputDir: not a directory" + s);
			System.exit(1);
		}
		outputDir = s;
	}

	/**
	 * joAlDiff.put("numDiffAlleles", numDiffAlleles)
	 * .put("numMissingsBase", numMissingsBase)
	 * .put("numMissingsTest", numMissingsTest)
	 * .put("numDifferencences", numMissingsBase + numMissingsTest + numDiffAlleles)
	 * .put("outSamples", outSamples);
	 * 
	 */
	private static void _writeOutput() throws IOException {
		String out = jaOut.toString(3);
		// String file = ctx.cfg.getS("file:manualInput") + ".json";
		String file = outputDir + inputFileName + ".json";
		System.out.println(out);
		System.out.println("========================== \nAllele Compare Report in file \n" + file
				+ "  \n==========================");
		Str.writeToFile(file, out);
	}

	/**
	 * @return array of objects (one for each row of TSV file)
	 */
	private static void _readSettings() {
		String file = ctx.cfg.getS("file:manualInput");
		try {
			ctx.jaListInputs = JSONArray.jsonArrayFromCSVFile(file, "\t");
		} catch (Exception e) {
			ctx.lg.error("_readSettings", e);
			System.exit(1);
		}
	}

	/******************************************
	 * init
	 * 
	 * @param args
	 */
	private static void init(String[] args) throws Exception {
		ctx.cfg = new JSONObject();
		// ctx.cfg.setBeanProperties(ctx); //TO RESTORE??
		// decrypt_password_key(ctx.cfg); //TO RESTORE
		CgMlstCompare.ctx = ctx;

		String inputFile = "";
		if (args.length > 0) {
			inputFile = args[0];
		} else {
			dirClass = EfsaCompatibility.class.getResource(".").getPath();
			String dirProject = dirClass + "../../../../../../";
			String dirEtc = dirProject + "etc/";
			inputFile = dirEtc +
			// "EfsaOhWgs/comparealleles/exampleOfCompareInput.tsv";
					"EfsaOhWgs/comparealleles/exampleOfCompareInputEXTENDED.tsv";
		}
		inputFilePath = inputFile;
		inputFileName = Str.getFileNameFromPath(inputFilePath);

		ctx.cfg.put("file:manualInput", inputFile); // "/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/comparealleles/exampleOfCompareInput.tsv");
	}
}// =====================================================================

/**
 * 
 */
class CgMlstCompare {
	static EfsaOhWgsContext ctx = new EfsaOhWgsContext();
	static String file;
	static String cgmlstschema;
	static JSONObject joIn, joOut;
	private static String species = "";

	static final long ListeriaNumLociInSchema = 1748;
	static final String ListeriaName = "L.monocytogenes";
	static final long NotListeriaNumLociInSchema = 1800; // TODO: change
	static final String NotListeriaName = "Salmonella or STEC"; // TODO: change

	static long MIN_ALLELES_DIFF = 0, MAX_ALLELES_DIFF = 0, MAX_DIFF = 0;
	static String thresholdNames = "MIN_ALLELES_DIFF,MAX_ALLELES_DIFF,MAX_DIFF";
	static JSONObject thresholds = new JSONObject(thresholdNames, 0, 0, 0),
			lmThresholds = new JSONObject(thresholdNames, 4, 7, 10),
			stecThresholds = new JSONObject(thresholdNames, 5, 10, 15),
			salmThresholds = new JSONObject(thresholdNames, 5, 10, 15);
	private static JSONObject joAlBase;
	private static JSONObject joAlTest;

	/**
	 * For test purpose
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		checkAllelicProfiles(
				"/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/comparealleles/chewbbaca_alleles_base.tsv",
				"/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/comparealleles/chewbbaca_alleles_test.tsv");
	}

	/**
	 * 
	 * @param s
	 */
	public static void checkAllelicProfiles(String fileBase, String fileTest) {

		try {
			init(fileBase, fileTest);
			_getDiff(fileBase, fileTest);
			_getCompatibleResult();

			// String compatbility = _getCompatibleResult(numMissingsBase,
			// numMissingsTest, numDiffAlleles);
			/*
			 * joAlCompareReport
			 * .put("fileBase", fileBase)
			 * .put("fileTest", fileTest)
			 * .put("compatbility", compatbility)
			 * .put("smplBase", smplBase)
			 * .put("smplTest", smplTest)
			 * .put("numMissingsBase", numMissingsBase)
			 * .put("numMissingsTest", numMissingsTest)
			 * .put("numDiffAlleles", numDiffAlleles)
			 * .put("numDifferencences", numMissingsBase + numMissingsTest +
			 * numDiffAlleles);
			 */
			ctx.cfg.put("joAllelesCompareReport", joOut);

		} catch (NumberFormatException e) {
			ctx.lg.error("checkAllelicProfiles: allelic profile must be in CRC32 format.", e);
			System.exit(1);
		} catch (Exception e) {
			ctx.lg.error("checkAllelicProfiles: problem with file:alleles" + fileTest, e);
		}
	}

	private static void init(String fileBase, String fileTest) {
		joIn = ctx.joInput;
		joOut = new JSONObject(joIn).prune();
		ctx.joAlCompareReport = joOut;

		try {
			joAlBase = JSONArray.jsonArrayFromCSVFile(fileBase, "\t").getJSONObject(0);
			joAlTest = JSONArray.jsonArrayFromCSVFile(fileTest, "\t").getJSONObject(0);

			joOut.put("numLociInSchema", joAlBase.length() - 1);

			if (joAlBase.length() != joAlTest.length()) {
				ctx.lg.error(fileBase + "," + fileTest + " has different number of loci");
			}

			species = joIn.getS("species");
			if (!species.matches("^(L.monocytogenes|STEC|Salmonella)$")) {
				ctx.lg.error("species not indicated: " + species);
				System.exit(1);
			}
			if (_isListeriaMonocytogenes()) {
				thresholds = lmThresholds;
			} else {
				thresholds = stecThresholds;
			}
			_setManualThresholds();
		} catch (JSONException | IOException e) {
				String error=joIn.qq("init: problems with baseAllelicProfileFile='$baseAllelicProfileFile' and testAllelicProfileFile='$testAllelicProfileFile'");
				joOut.put("ERROR", error);
				ctx.lg.error(error,e);

		}

	}

	private static void _setManualThresholds() throws NumberFormatException {
		for (String thName : thresholdNames.split(",")) {
			String thValString = joIn.getS(thName);
			if (!thValString.isEmpty()) {
				thresholds.put(thName, Integer.parseInt(thValString));
				joOut.put("log:" + thName, "manual choice " + thName);
			}
		}
	}

	private static void _getDiff(String fileBase, String fileTest)
			throws JSONException, NumberFormatException, Exception {

		String smplBase = "", smplTest = "", alBase = "", alTest = "";
		long numDiffAlleles = 0, numMissingsBase = 0, numMissingsTest = 0;
		JSONObject jo = ctx.joAlCompareReport;

		for (String locus : joAlBase.keyNames()) {
			alBase = joAlBase.getS(locus);
			alTest = joAlTest.getS(locus);

			if (locus.matches(".*FILE.*")) {
				smplBase = alBase;
				smplTest = alTest;
				continue;

			} else if (alBase.equals(alTest)) {
				continue;
			}

			long intAlBase = Long.parseLong(alBase);
			long intAlTest = Long.parseLong(alTest);

			if (intAlBase == 0) {
				numMissingsBase++;
			} else if (intAlTest == 0) {
				numMissingsTest++;
			} else {
				numDiffAlleles++;
			}
			jo.put(locus, "base:" + alBase + ", test:" + alTest);
		}
		jo
				.put("smplBase", smplBase)
				.put("smplTest", smplTest)
				.put("numMissingsBase", numMissingsBase)
				.put("numMissingsTest", numMissingsTest)
				.put("numDiffAlleles", numDiffAlleles)
				.put("numDifferencences", numMissingsTest + numDiffAlleles);

	}

	/**
	 * threshold perfect: perfect match
	 * threshold acceptable: Num. missing Loci + different called alleles <= 10
	 * threshold warning: 4 < different called alleles < 7
	 * threshold fail: different called alleles (loci called on base and test) => 7
	 * threshold fail: Num. missing Loci + different called alleles > 10
	 */
	private static void _getCompatibleResult() {
		JSONObject jo = ctx.joAlCompareReport;
		long numMissingsBase = jo.getLong("numMissingsBase"),
				numMissingsTest = jo.getLong("numMissingsTest"),
				numDiffAlleles = jo.getLong("numDiffAlleles"),
				MAX_DIFF = 10, MAX_ALLELES_DIFF = 7, MIN_ALLELES_DIFF = 4;

		if (_isListeriaMonocytogenes()) {
			jo.put("schema", ListeriaName);
			MAX_DIFF = 10;
			MAX_ALLELES_DIFF = 7;
			MIN_ALLELES_DIFF = 4;
		} else {
			jo.put("schema", NotListeriaName);
			MAX_DIFF = 15;
			MAX_ALLELES_DIFF = 10;
			MIN_ALLELES_DIFF = 5;
		}
		String compatbility = "";
		if (numMissingsBase == 0 && numMissingsTest == 0 && numDiffAlleles == 0) {
			compatbility = "Perfect";
		} else if (numDiffAlleles >= MAX_ALLELES_DIFF) {
			compatbility = "Fail";
		} else if (numMissingsTest + numDiffAlleles > MAX_DIFF) {
			compatbility = "Fail";
		} else if (numDiffAlleles > MIN_ALLELES_DIFF) {
			compatbility = "Warning";
		} else {
			compatbility = "Acceptable";
		}
		jo.put("compatbility", compatbility);
		jo.put("thresholds", lmThresholds.toString());

	}

	private static boolean _isListeriaMonocytogenes() {
		return _isSpecies(ListeriaName, ListeriaNumLociInSchema);
	}

	private static boolean _isSpecies(String speciesName, long requestedNumLociInSchema) {
		String species = ctx.joInput.getS("species");
		if (!species.isEmpty()) {
			return (species.equals(speciesName));
		}

		long numLociInSchema = ctx.joAlCompareReport.getLong("numLociInSchema");
		return (numLociInSchema == 1748);
	}

}
// =====================================================================
