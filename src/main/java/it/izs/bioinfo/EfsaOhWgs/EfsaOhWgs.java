package it.izs.bioinfo.EfsaOhWgs;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.digest.DigestUtils;

import it.izs.api.Bioinfoquery;
import it.izs.api.general.GenericMain;
import it.izs.utils.Crypt;
import it.izs.utils.JSONArray;
import it.izs.utils.JSONException;
import it.izs.utils.JSONObject;
import it.izs.utils.ShellUtils;
import it.izs.utils.Str;

public class EfsaOhWgs extends GenericMain {

	static EfsaOhWgsContext ctx = new EfsaOhWgsContext();
	static Bioinfoquery q;
	static ShellUtils sh = new ShellUtils();
	static String dirClass = ".";

	/******************************************
	 * MAIN WORKFLOW
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		init(args);
		ctx.joSubmission = new JSONObject();
		// _importJsonTemplate();
		JSONArray ja = _readSettings();
		ctx.jaListInputs = ja;
		System.out.println("==========================INPUT: ==========================");
		System.out.println(ja.toString(3));
		System.out.println("==========================OUTPUT:==========================");

		for (int i = 0; i < ja.length(); i++) {
			ctx.joInput = ja.getJSONObject(i);
			ctx.joSubmission = new JSONObject();

			_manualValues(args);
			RawDataMgr.main(args);
			AssemblyMgr.main(args);
			TypingMgr.main(args);
			CgMlstMgr.main(args);
			// JsonFiller.main(args);

			_writeJsonOutput();
		}
		// printJson();
		// ctx.lg.stopOnError();
	}

	/**
	 * @return array of objects (one for each row of TSV file)
	 */
	private static JSONArray _readSettings() {
		ctx.cfg
				.put("file:manualInput", "/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/exampleOfManualInput.tsv");
		String file = ctx.cfg.getS("file:manualInput");

		try {
			return JSONArray.jsonArrayFromCSVFile(file, "\t");
		} catch (Exception e) {
			ctx.lg.error("_readSettings", e);
			System.exit(1);
		}
		// getJSONObject(0);
		return null;
	}

	/**
	 * 
	 * @return Object with all filled input
	 * 
	 *         private static JSONObject _readListOfFiles() {
	 * 
	 *         for (String k : joJsonPositions.keyNames()) {
	 *         String keyValue = joJsonPositions.getS(k);
	 *         jo.putPath(k, joManualValues.get(keyValue));
	 *         }
	 *         System.out.println(ctx.cfg.toString(4));
	 *         }
	 */
	/**
	 * localRawReadId sampling_local_id isolation_local_id isolateSpecieCode
	 * sampling_country_id sampling_year sampling_matrix_code
	 * sampling_matrix_free_text libraryLayoutCode MLSTSequenceType.Software
	 * rawr1234 smpl1234 isol1234 RF-00003072-PAR IT 2019 A02QE FORMAGGIO 2 mlst
	 * 2.23.0
	 * 
	 * @param args
	 */
	private static void _manualValues(String[] args) {
		JSONObject jo = ctx.joSubmission;
		try {
			//
			// System.out.println(file +
			// "\n----------\n" +
			// ctx.joInput.toString(3) +
			// "\n----------\n" +
			// "\n----------\n" +
			// "\n----------\n"
			// );
			// trasnform libraryLayoutCode in integer
			//
			ctx.joInput.put("libraryLayoutCode",
					Integer.valueOf(ctx.joInput.getS("libraryLayoutCode")));

			JSONObject joJsonPositions = new JSONObject()
					.put("payload.publicRawReadCreate.localRawReadId", "localRawReadId")
					.put("payload.publicRawReadCreate.isolateSpecieCode", "isolateSpecieCode")
					.put("payload.publicRawReadCreate.libraryLayoutCode", "libraryLayoutCode")
					.put("payload.result.Results.ParamCode.MLSTSequenceType.Software", "MLSTSequenceType.Software")
					.put("payload.result.AnalyticalPipelineInfoTag", "AnalyticalPipelineInfoTag")
					.put("payload.result.localRawReadId", "localRawReadId");
			if (_addEpiData()) {
				joJsonPositions
						.put("payload.publicEpidemiologicalDataCreate.localRawReadId", "localRawReadId")
						.put("payload.publicEpidemiologicalDataCreate.samplingData.sampleLocalId", "sampleLocalId")
						.put("payload.publicEpidemiologicalDataCreate.samplingData.samplingCountryId",
								"samplingCountryId")
						.put("payload.publicEpidemiologicalDataCreate.samplingData.samplingYear", "samplingYear")
						.put("payload.publicEpidemiologicalDataCreate.samplingData.samplingMatrixCode",
								"samplingMatrixCode")
						.put("payload.publicEpidemiologicalDataCreate.samplingData.samplingMatrixFreeText",
								"samplingMatrixFreeText")
						.put("payload.publicEpidemiologicalDataCreate.isolationData.isolationLocalId",
								"isolationLocalId");
			}

			for (String k : joJsonPositions.keyNames()) {
				String keyValue = joJsonPositions.getS(k);
				String value = ctx.joInput.getS(keyValue);
				if (value.isEmpty()) {
					ctx.lg.error("_manualValues: mandatory value is missing:" + keyValue );
					System.out.println(jo.toString(3));
					System.exit(1);
				}
				jo.putPath(k, ctx.joInput.get(keyValue));
			}
		} catch (Exception e) {
			ctx.lg.error("_manualValues", e);
			System.out.println(jo.toString(3));
			System.exit(1);
		}
	}

	private static boolean _addEpiData() {
		return ctx.joInput.getS("cfg:epidataSubmission").equals("Y");
	}

	/******************************************
	 * importJsonTemplate
	 * the template for EOH
	 * 
	 * @throws Exception
	 * 
	 */
	private static void _importJsonTemplate() throws Exception {
		JSONObject jo = JSONObject
				.jsonObjectFromFile("/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/TypingTemplate.json");
	}

	/**
	 * write API, CLI and allelic profile files in the format of:
	 * 
	 * api_localRawReadId.json for API
	 * 
	 * and
	 * cli_localRawReadId_result.json
	 * cli_localRawReadId_publicRawReadCreate.json
	 * cli_localRawReadId_publicEpidemiologicalDataCreate.json (if chosen)
	 * for CLI
	 * 
	 * and
	 * alleles_localRawReadId.tsv for allelic profile
	 */
	private static void _writeJsonOutput() {
		String localRawReadId = ctx.joInput.getS("localRawReadId");
		String outDir = ctx.joInput.getS("cfg:outputDir");
		JSONObject payload = ctx.joSubmission.getJO("payload");

		String filename = "";
		try {
			// API
			filename = outDir + "api_" + localRawReadId + ".json";
			Str.writeToFile(filename, ctx.joSubmission.toString(3));
			// CLI
			String cliFilenames = "result,publicRawReadCreate";
			if (_addEpiData()) {
				cliFilenames += ",publicEpidemiologicalDataCreate";
			}
			for (String key : cliFilenames.split(",")) {
				filename = outDir + "cli_" + localRawReadId + "_" + key + ".json";
				Str.writeToFile(filename,
						payload.getJO(key).toString(3));
			}
			// allelic profile
			filename = outDir + "alleles_" + localRawReadId + ".tsv";
			Str.writeToFile(filename, ctx.allelicProfileCrc32);
			// shell output
			System.out.println("files in " + outDir + "*" + localRawReadId + '*');

		} catch (IOException e) {
			ctx.lg.error("_writeJsonOutput: problems writing on file " + filename, e);
		}
	}

	/******************************************
	 * init
	 * 
	 * @param args
	 */
	private static void init(String[] args) throws Exception {
		// JSONObject cfg= importInternalCfg(); TO RESTORE
		JSONObject cfg = new JSONObject(); // TO REMOVE
		// overwriteJson(cfg, args);
		// dirClass = EfsaOhWgs.class.getResource(".").getPath();

		cfg.setBeanProperties(ctx);
		ctx.cfg = cfg;
		// decrypt_password_key(ctx.cfg); //TO RESTORE
		JsonFiller.ctx = ctx;
		RawDataMgr.ctx = ctx;
		AssemblyMgr.ctx = ctx;
		TypingMgr.ctx = ctx;
		CgMlstMgr.ctx = ctx;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private static JSONObject importInternalCfg() throws Exception {
		InputStream in = EfsaOhWgs.class.getResourceAsStream("/EfsaOhWgs/cfg.json");
		String content = Str.readStreamString(in);
		return new JSONObject(content);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private static JSONObject importInternalJson(String filename) throws Exception {

		InputStream in = EfsaOhWgs.class.getResourceAsStream(filename);
		String content = Str.readStreamString(in);
		return new JSONObject(content);
	}

	/**
	 * aggregates internal cfg.json file with additional json text from file or
	 * string
	 * java -jar app.jar cfg.json
	 * java -jar app.jar '{"key1":"value1","key2":"value2"}'
	 * values of key1 and key2 in cfg.json will be overwritten
	 * 
	 * @param args
	 */
	private static void overwriteJson(JSONObject cfg, String[] args) {
		if (args.length == 0) {
			ctx.lg.log("EFSA One Health WGS system  - ISA Contract \n\n " +
					"Accept a configuration file or text in JSON format. \n" +
					"");
			System.exit(0);
		}
		String s = args[0];

		if (s.matches("-help")) {
			ctx.lg.log("Configuration: \n" + cfg.toString(3));
			System.exit(0);
		}
		if (s.matches("-nofile")) {
			return;
		}

		try {
			JSONObject a;
			if (s.matches("^\\s*\\{.*$")) {
				a = new JSONObject(s);
			} else {
				if (!Str.fileExists(s)) {
					ctx.lg.error(": File non esiste: " + s);
					System.exit(0);
				}
				;
				a = JSONObject.jsonObjectFromFile(s);
			}
			for (String key : a.keyNames()) {
				cfg.put(key, a.get(key));
			}

		} catch (IOException e) {
			ctx.lg.error("Problem with File: " + s);
			System.exit(0);
		} catch (JSONException e) {
			ctx.lg.error("Not a JSON format: " + s);
			System.exit(0);
		}

	}

	/**
	 * set password decrypting values
	 * 
	 * @param cfg
	 */
	private static void decrypt_password_key(JSONObject cfg) {
		String cryptKey = cfg.getS("decrypt_password_key");

		try {
			setDecryptString(cfg, "jdbc_password", cryptKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setDecryptString(cfg.getJO("silabaccess"), "password", cryptKey);
		setDecryptString(cfg.getJO("genpatpostgres"), "password_postgres", cryptKey);
		setDecryptString(cfg.getJO("genpataccess"), "password", cryptKey);
	}

	/**
	 * lo decifra e lo riassegna all'hash se contiene password cifrata e base64
	 * ~".*=="
	 * 
	 * @param cfg
	 * @param keyName
	 * @param cryptKey
	 * @throws Exception
	 */
	private static void setDecryptString(JSONObject cfg, String keyName, String cryptKey) {

		String cryptValue = cfg.getS(keyName);
		if (cryptValue.matches(".*==$")) {
			// String val=Crypt.decrypt(cryptValue, cryptKey);
			try {
				cfg.put(keyName, Crypt.decrypt(cryptValue, cryptKey));
			} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
					| NoSuchPaddingException e) {
				ctx.lg.error("BAD decrypt_password_key=" + cryptKey);
				ctx.lg.error(e);
				System.exit(1);
			} catch (JSONException | NoSuchAlgorithmException e) {
				ctx.lg.error(e);
				System.exit(1);
			}
		} else {
			ctx.lg.log("password not decrypted:" + keyName);
		}
	}

}// =====================================================================

/**
 * 
 */
class JsonFiller {
	static EfsaOhWgsContext ctx;
	static String schema = "";

	public static void main(String[] args) {
		checkSchema();
		checkSchemaConditionals();
		// System.out.println("JsonFiller");
	}

	private static void initSchema() throws IOException {
		schema = Str.readFileString("/opt/adri/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/JsonSchema.json");
	}

	/**
	 * 
	 */
	private static void checkSchema() {
		// try {
		// initSchema();
		// // Create a new schema in a JSON object.
		// JSONObject schemaJson =
		// JSONObject.jsonObjectFromFile("/opt/adri/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/JsonSchema.json");
		// File input=new
		// File("/opt/adri/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/EColiExample.json");
		// File input2=new
		// File("/ws/bioinfo/genpat-bioinfotools/etc/EfsaOhWgs/TypingTemplate.json");
		//
		// SchemaStore schemaStore = new SchemaStore(); // Initialize a SchemaStore.
		// //Schema schema = schemaStore.loadSchema(schemaJson.toMap()); // Load the
		// schema.
		// //Schema schema = schemaStore.loadSchemaJson(schemaJson.toString()); // Load
		// the schema.
		//
		// Validator validator = new Validator(); // Create a validator.
		//
		// System.out.println("\n------------------\n");
		// System.out.println(input.toString());
		// validator.validate ( schema, input ); // Will not throw an exception.
		// System.out.println("\n------------------\n");
		// System.out.println(input2.toString());
		// validator.validate ( schema, input2 ); // Will throw a ValidationException.
		//
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (ValidationException e) {
		// e.printStackTrace();
		// } catch (SchemaException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 
	 */
	private static void checkSchemaConditionals() {

		// if(libraryLayoutCode==2) "fastQ2FileName", "fastQ2Md5" mandatory
		// EColi only
		// if(genes ... ) "fastQ2FileName", "fastQ2Md5" mandatory

	}

}// =====================================================================

/**
 * 
 */
class RawDataMgr {
	static EfsaOhWgsContext ctx = new EfsaOhWgsContext();

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JSONObject jo = ctx.joSubmission;
		String file = "";

		String f1 = ctx.joInput.getS("file:R1");
		String f2 = ctx.joInput.getS("file:R2");
		rawreadsMd5(f1, f2, jo);

		_rawreadsQC();

		// System.out.println("JSON\n" + jo.toString(3));
		// System.out.println("RawDataMgr\n"+filename + "\n"+md5sum);
	}

	private static void _rawreadsQC() {
		JSONObject jo = ctx.joSubmission;
		String file = "";

		file = ctx.joInput.getS("file:fqc");
		if (!file.isEmpty()) {
			fastqcExtract(file, jo);
			return;
		}
		file = ctx.joInput.getS("file:fastp");
		if (!file.isEmpty()) {
			fastp(file, jo);
			return;
		}
		ctx.lg.error("_rawreadsQC: file:fastp and file:fqc are empty.");
		System.exit(1);

	}

	/**
	 * 
	 * @param file1 Raw reads File R1
	 * @param file2 Raw reads File R2
	 */
	public static boolean rawreadsMd5(String file1, String file2, JSONObject jo) {
		try {
			if (!Str.fileExists(file1)) {
				ctx.lg.error("rawreadsMd5: file does not exists:" + "'" + file1 + "'");
				return false;
			}
			String md5sum = DigestUtils.md5Hex(Str.readFileString(file1));

			jo.putPath("payload.publicRawReadCreate.fastQ1FileName",
					Str.getFileNameFromPath(file1));
			jo.putPath("payload.publicRawReadCreate.fastQ1Md5",
					md5sum);

			if (Str.isEmpty(file2)) {
				if (ctx.libraryLayoutCode == 1) {
					return true;
				}
				ctx.lg.error("libraryLayoutCode == 2, but file2 does not exists:" + "'" + file2 + "'");
				return false;
			}
			if (!Str.fileExists(file2)) {
				ctx.lg.error("file R2 does not exists:" + "'" + file2 + "'");
				return false;
			}

			jo.putPath("payload.publicRawReadCreate.fastQ2FileName",
					Str.getFileNameFromPath(file2));
			jo.putPath("payload.publicRawReadCreate.fastQ2Md5",
					DigestUtils.md5Hex(Str.readFileString(file2)));

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Example
	 * #Sample_name Total_reads Total_Mbases Min_length Max_length Mean_length
	 * Q30_reads Min_avgQual Max_avgQual Mean_avgQual
	 * DS1-DT1-raw123 1708716 254.6 149 149 149 92.11
	 * 14.8456375838926 35.8657718120805 33.77
	 * 
	 * @param file Fastqc File
	 */
	public static boolean fastqcExtract(String file, JSONObject jo) {
		if (!Str.fileExists(file)) {
			ctx.lg.error("fastqcExtract: file does not exists:" + "'" + file + "'");
			return false;
		}
		try {
			JSONArray ja = JSONArray.jsonArrayFromCSVFile(file, ",");
			Double f;
			f = Double.valueOf(ja.get(0, "Mean_length"));
			jo.putPath("payload.result.QualityCheck.Fastp.ReadMeanLength", f);// ja.get(0, "Mean_length"));

			f = Double.valueOf(ja.get(0, "Q30_reads"));
			jo.putPath("payload.result.QualityCheck.Fastp.Q30Rate", f);// ja.get(0, "Q30_reads"));
			f = Double.valueOf(ja.get(0, "Total_Mbases")) * 1000;
			jo.putPath("payload.result.QualityCheck.Fastp.TotalBases", f);// ja.get(0, "Total_Mbases"));

			// System.out.println(ja.get(0, "Q30_reads"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * https://github.com/OpenGene/fastp
	 * Example
	 * see https://opengene.org/fastp/fastp.json
	 * "summary": {
	 * "fastp_version": "0.23.1",
	 * "sequencing": "paired end (151 cycles + 151 cycles)",
	 * "before_filtering": {
	 * "total_reads":4037198,
	 * "total_bases":532032018,
	 * "q20_bases":502709959,
	 * "q30_bases":482359449,
	 * "q20_rate":0.944887,
	 * "q30_rate":0.906636,
	 * "read1_mean_length":131,
	 * "read2_mean_length":131,
	 * "gc_content":0.394562
	 * },
	 * "after_filtering": {
	 * "total_reads":3958118,
	 * "total_bases":520380868,
	 * "q20_bases":495541169,
	 * "q30_bases":476193134,
	 * "q20_rate":0.952266,
	 * "q30_rate":0.915086,
	 * "read1_mean_length":131,
	 * "read2_mean_length":131,
	 * "gc_content":0.393995
	 * 
	 * @param file fastp json file
	 */
	private static boolean fastp(String file, JSONObject jo) {
		if (!Str.fileExists(file)) {
			ctx.lg.error("fastp: file does not exists:" + "'" + file + "'");
			return false;
		}
		try {
			JSONObject jofp = new JSONObject(Str.readFileString(file));
			String val;

			val = jofp.getPathS("summary.after_filtering.read1_mean_length");
			if (!val.isEmpty()) {
				Double f = Double.valueOf(val);
				jo.putPath("payload.result.QualityCheck.Fastp.ReadMeanLength", f);

			} else {
				ctx.lg.error("fastp: no ReadMeanLength found " + file);
			}

			val = jofp.getPathS("summary.after_filtering.q30_rate");
			if (!val.isEmpty()) {
				Double f = Double.valueOf(val.substring(0, 4));
				jo.putPath("payload.result.QualityCheck.Fastp.Q30Rate", f);

			} else {
				ctx.lg.error("fastp: no Q30Rate found " + file);
			}

			val = jofp.getPathS("summary.after_filtering.total_bases");
			if (!val.isEmpty()) {
				Integer f = Integer.valueOf(val.replaceAll("\\.", ""));
				jo.putPath("payload.result.QualityCheck.Fastp.TotalBases", f);

			} else {
				ctx.lg.error("fastp: no TotalBases found " + file);
			}

			// System.out.println(ja.get(0, "Q30_reads"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

}// =====================================================================

/**
 * 
 */
class AssemblyMgr {
	static EfsaOhWgsContext ctx = new EfsaOhWgsContext();

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		_checkQC();
		checkCoverageBowtie(ctx.joInput.getS("file:bowtie"));
	}

	private static void _checkQC() {
		String file = "";

		file = ctx.joInput.getS("file:quast");
		if (!file.isEmpty()) {
			checkQuast(file);
			return;
		}
		file = ctx.joInput.getS("file:checkm");
		if (!file.isEmpty()) {
			checkCheckm(file);
			return;
		}
		ctx.lg.error("_checkQC: file:quast and file:checkm are empty.");
		System.exit(1);

		checkQuast(ctx.joInput.getS("file:quast"));

	}

	/**
	 * Assembly # contigs Largest contig Total length N50 N75 L50 L75 # N's per 100
	 * kbp
	 * DS1-DT1-raw123_spades_scaffolds_L200 17 816329 3053558
	 * 729781 434356 2 4 0.23
	 * 
	 * @param file Quast file
	 */
	public static boolean checkQuast(String file) {
		JSONObject jo = ctx.joSubmission;
		try {
			if (!Str.fileExists(file)) {
				ctx.lg.error("checkQuast: file does not exists:" + "'" + file + "'");
				return false;
			}
			JSONArray ja = JSONArray.jsonArrayFromCSVFile(file, "\t");
			Integer f;
			f = Integer.valueOf(ja.get(0, "N50"));
			jo.putPath("payload.result.QualityCheck.AssemblyQualityStatistics.AssemblyStatistics.N50Contigs",
					f);// ja.get(0, "Mean_length"));

			f = Integer.valueOf(ja.get(0, "Total length"));
			jo.putPath("payload.result.QualityCheck.AssemblyQualityStatistics.AssemblyStatistics.GenomeSize",
					f);// ja.get(0, "Mean_length"));

			f = Integer.valueOf(ja.get(0, "# contigs"));
			jo.putPath("payload.result.QualityCheck.AssemblyQualityStatistics.AssemblyStatistics.NumberOfContigs",
					f);// ja.get(0, "Mean_length"));

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * C_pass_contigs
	 * {'GC': 0.5014077148738508, 'GC std': 0.05200201171151964,
	 * 'Genome size': 5466661, '# ambiguous bases': 0, '# scaffolds': 330,
	 * '# contigs': 330, 'Longest scaffold': 325790, 'Longest contig': 325790, 'N50
	 * (scaffolds)': 146492,
	 * 'N50 (contigs)': 146492, 'Mean scaffold length': 16565.639393939393,
	 * 'Mean contig length': 16565.639393939393, 'Coding density':
	 * 0.8737706618354422,
	 * 'Translation table': 11, '# predicted genes': 5409}
	 * 
	 * @param file checkm/bin_stats.analyze.tsv file
	 */
	public static boolean checkCheckm(String file) {
		JSONObject jo = ctx.joSubmission;
		try {
			if (!Str.fileExists(file)) {
				ctx.lg.error("checkQuast: file does not exists:" + "'" + file + "'");
				return false;
			}
			String[] a = Str.readFileStringArray(file);
			JSONObject jocm = new JSONObject(a[0].replaceAll("^.*?\t", ""));

			Integer f;
			f = Integer.valueOf(jocm.getS("N50 (contigs)"));
			jo.putPath("payload.result.QualityCheck.AssemblyQualityStatistics.AssemblyStatistics.N50Contigs",
					f);

			f = Integer.valueOf(jocm.getS("Genome size"));
			jo.putPath("payload.result.QualityCheck.AssemblyQualityStatistics.AssemblyStatistics.GenomeSize",
					f);

			f = Integer.valueOf(jocm.getS("# contigs"));
			jo.putPath("payload.result.QualityCheck.AssemblyQualityStatistics.AssemblyStatistics.NumberOfContigs",
					f);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * CMP_ID,SAMPLE_DS,NUM_MAPPED_READS,MIN_VCOV,MAX_VCOV,COV,H_COV,NOTE,PERC_IUPAC,PERC_NS,CONSENSUS_LENGTH
	 * raw123,13528998,1557893,0,154,76.5462,0.989174,mapping on
	 * GCF_000438605.1,0.001,1.143,2988880
	 * 
	 * @param file Bowtie meta file
	 */
	public static boolean checkCoverageBowtie(String file) {
		JSONObject jo = ctx.joSubmission;
		try {
			if (!Str.fileExists(file)) {
				ctx.lg.error("checkCoverageBowtie: file does not exists:" + "'" + file + "'");
				return false;
			}
			JSONArray ja = JSONArray.jsonArrayFromCSVFile(file, ",");
			Integer f;
			f = Integer.valueOf(ja.get(0, "COV").replaceAll("\\..*$", ""));
			jo.putPath("payload.result.QualityCheck.AssemblyQualityStatistics.AssemblyCoverage", f);
			return true;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}// =====================================================================

/**
 * 
 */
class TypingMgr {
	static EfsaOhWgsContext ctx = new EfsaOhWgsContext();

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		checkMLST();

		checkSerotype();
		checkStxSubtyping();
		checkPathotype();
	}

	private static void checkMLST() {
		if (ctx.joInput.getS("file:mlst").isEmpty()) {
			ctx.lg.error("checkMLST: file:mlst empty ");
			System.exit(1);
		}
		checkMlstSeeman(ctx.joInput.getS("file:mlst"));
	}

	/**
	 * 
	 */
	private static void checkSerotype() {
		if (!ctx.joInput.getS("file:seqsero").isEmpty()) {
			_serotypeSeqsero();
			return;
		}
		if (!ctx.joInput.getS("file:sistr").isEmpty()) {
			_serotypeSistr();
			return;
		}
		if (!ctx.joInput.getS("file:seq_typing").isEmpty()) {
			_serotypeSeqTyping();
			return;
		}
		if (!ctx.joInput.getS("isolateSpecieCode").equals("RF-00000251-MCG")) {
			ctx.lg.error("_serotypeSeqsero: file:mlst empty for localRawReadId:" + ctx.joInput.getS("localRawReadId"));
			System.exit(1);
		}
	}

	private static void checkStxSubtyping() {
		if (!ctx.joInput.getS("file:stx_typing").isEmpty()) {
			_stxSubtypingSeqTyping();
			return;
		}

		// if(! ctx.joInput.getS("isolateSpecieCode").equals("RF-00000251-MCG")){
		// ctx.lg.error("_serotypeSeqsero: file:mlst empty for localRawReadId:" +
		// ctx.joInput.getS("localRawReadId"));
		// System.exit(1);
		// }
	}

	/**
	 * https://github.com/B-UMMI/seq_typing
	 * stx_subtyping_report/stx_subtyping_report.txt
	 */
	private static void _stxSubtypingSeqTyping() {
		JSONObject jo = ctx.joSubmission;
		String file = ctx.joInput.getS("file:stx_typing");
		try {
			if (file.isEmpty()) {
				return;
			}
			if (!Str.fileExists(file)) {
				ctx.lg.error("_stxSubtypingSeqTyping: problems with file:stx_typing  " + file);
				return;
			}

			String serotype = Str.readFileString(file).replaceAll("\r?\n", "");
			if (serotype.isEmpty()) {
				ctx.lg.error("_stxSubtypingSeqTyping: \"PredictedStxType\" empty in  file:stx_typing  " + file);
				return;
			}
			jo.putPath("payload.result.Results.ParamCode.PredictedStxType", serotype);

		} catch (Exception e) {
			ctx.lg.error("_stxSubtypingSeqTyping: problems with file:stx_typing  " + file);
			System.exit(1);
		}
	}

	/**
	 * https://github.com/denglab/SeqSero2
	 */
	private static void _serotypeSeqsero() {
		try {
			JSONObject jo = ctx.joSubmission;
			String file = ctx.joInput.getS("file:seqsero");
			if (file.isEmpty()) {
				return;
			}
			if (!Str.fileExists(file)) {
				ctx.lg.error("_serotypeSeqsero: problems with file:seqsero  " + file);
				return;
			}

			JSONArray ja = JSONArray.jsonArrayFromCSVFile(file, "\t");
			String serotype = ja.get(0, "Predicted serotype");
			if (serotype.isEmpty()) {
				ctx.lg.error("_serotypeSeqsero: \"Predicted serotype\" empty in  file:seqsero  " + file);
				return;
			}
			jo.putPath("payload.result.Results.ParamCode.PredictedSerotype.Software", "SeqSero2");
			jo.putPath("payload.result.Results.ParamCode.PredictedSerotype.Serotype",
					serotype);
		} catch (Exception e) {
			ctx.lg.error("_serotypeSeqsero: file:seqsero empty ", e);
			System.exit(1);
		}
	}

	/**
	 * https://github.com/phac-nml/sistr_cmd
	 */
	private static void _serotypeSistr() {
		try {
			JSONObject jo = ctx.joSubmission;
			String file = ctx.joInput.getS("file:sistr");
			if (file.isEmpty()) {
				return;
			}
			if (!Str.fileExists(file)) {
				ctx.lg.error("_serotypeSistr: problems with file:sistr  " + file);
				return;
			}

			JSONArray ja = JSONArray.jsonArrayFromCSVFile(file, "\t");
			String serotype = ja.get(0, "serovar");
			if (serotype.isEmpty()) {
				ctx.lg.error("_serotypeSistr: \"Predicted serotype\" empty in  file:sistr  " + file);
				return;
			}
			jo.putPath("payload.result.Results.ParamCode.PredictedSerotype.Software", "sistr");
			jo.putPath("payload.result.Results.ParamCode.PredictedSerotype.Serotype",
					serotype);
		} catch (Exception e) {
			ctx.lg.error("_serotypeSistr: file:mlst empty ", e);
			System.exit(1);
		}
	}

	/**
	 * https://github.com/B-UMMI/seq_typing
	 */
	private static void _serotypeSeqTyping() {
		try {
			JSONObject jo = ctx.joSubmission;
			String file = ctx.joInput.getS("file:seq_typing");
			if (file.isEmpty()) {
				return;
			}
			if (!Str.fileExists(file)) {
				ctx.lg.error("_serotypeSeqTyping: problems with file:seq_typing  " + file);
				return;
			}

			String serotype = Str.readFileString(file).replaceAll("\r?\n", "");
			if (serotype.isEmpty()) {
				ctx.lg.error("_serotypeSeqTyping: \"Predicted serotype\" empty in  file:seq_typing  " + file);
				return;
			}
			jo.putPath("payload.result.Results.ParamCode.PredictedSerotype.Software", "seq_typing");
			jo.putPath("payload.result.Results.ParamCode.PredictedSerotype.Serotype",
					serotype);
		} catch (Exception e) {
			ctx.lg.error("_serotypeSeqTyping: file:seq_typing empty ", e);
			System.exit(1);
		}
	}

	///////////////////////////////////////////////////////////

	/**
	 * Example
	 * 
	 * "PredictedPathotype": {
	 * "Pathotype": "NA",
	 * "VeroToxin": {
	 * "VT1Positive": false,
	 * "VT2Positive": true
	 * },
	 * "AdhesionGenes": {
	 * "eaePositive": false,
	 * "aatPositive": false,
	 * "aggRPositive": false,
	 * "aaiCPositive": false
	 * },
	 * "Software": "patho_typing",
	 * "GeneList": [
	 * {
	 * "GeneName": "stx2fb",
	 * "Identity": 100,
	 * "Coverage": 98.48
	 * },
	 * {
	 * "GeneName": "stx2a",
	 * "Identity": 99.79,
	 * "Coverage": 94.26
	 * }
	 * ]
	 * }
	 * 
	 */
	private static void checkPathotype() {
		if (!ctx.joInput.getS("dir:patho_typing").isEmpty()) {
			_pathoTyping();
			return;
		}
		if (!ctx.joInput.getS("isolateSpecieCode").equals("RF-00000251-MCG")) {
			ctx.lg.error("_serotypeSeqsero: file:mlst empty for localRawReadId:" + ctx.joInput.getS("localRawReadId"));
			System.exit(1);
		}
	}

	/**
	 * https://github.com/B-UMMI/patho_typing
	 * //patho_typing/patho_typing.extended_report.txt
	 * port of patho_typing.py
	 * 
	 * - Read Pathotype from patho_typing.report.txt
	 * - Read "list of genes" from patho_typing.extended_report.txt
	 * - Calculate VeroToxin and AdhesionGenes from "list of genes"
	 * according to https://pubmed.ncbi.nlm.nih.gov/10994531/
	 * - Read GeneList from rematchModule_report.txt
	 */
	private static void _pathoTyping() {
		JSONObject jopatho = new JSONObject("{\n" + //
				"  \"dictAdhesionGeneLookup\": {\n" + //
				"    \"EAE\": \"eaePositive\",\n" + //
				"    \"AATA\": \"aatPositive\",\n" + //
				"    \"AGGR\": \"aggRPositive\",\n" + //
				"    \"AAIC\": \"aaiCPositive\"\n" + //
				"  },\n" + //
				"  \"dictVeroToxinsLookup\": {\n" + //
				"    \"STX2A\": \"VT2Positive\",\n" + //
				"    \"STX2B\": \"VT2Positive\",\n" + //
				"    \"STX2FA\": \"VT2Positive\",\n" + //
				"    \"STX2FB\": \"VT2Positive\",\n" + //
				"    \"STX1A\": \"VT1Positive\",\n" + //
				"    \"STX1B\": \"VT1Positive\"\n" + //
				"  },\n" + //
				"  \"dictPredictedPathotype\": {},\n" + //
				"  \"dictAdhesionGenes\": {\n" + //
				"    \"eaePositive\": false,\n" + //
				"    \"aatPositive\": false,\n" + //
				"    \"aggRPositive\": false,\n" + //
				"    \"aaiCPositive\": false\n" + //
				"  },\n" + //
				"  \"dictVeroToxins\": {\n" + //
				"    \"VT1Positive\": false,\n" + //
				"    \"VT2Positive\": false\n" + //
				"  }\n" + //
				"}");

		try {
			JSONObject joParamCode = ctx.joSubmission.getPath("payload.result.Results.ParamCode"),
					dictPredictedPathotype = new JSONObject(),
					lstGeneNames = new JSONObject();
			String file = "", pathotype = "",
					dir = ctx.joInput.getS("dir:patho_typing");
			String[] lines;
			//
			// Software + Pathotype
			// from patho_typing.report.txt
			//
			file = dir + "patho_typing.report.txt";
			pathotype = Str.readFileStringArray(file)[0].replaceAll("^\\s*", "").replaceAll("\\s*$", "");
			dictPredictedPathotype.put("Pathotype", pathotype);
			dictPredictedPathotype.put("Software", "patho_typing");
			//
			// list of genes
			// from patho_typing.extended_report.txt
			//
			file = dir + "patho_typing.extended_report.txt";
			lines = Str.readFileStringArray(file);
			boolean genesZone = false;
			for (String l : lines) {
				if (genesZone && !l.isEmpty()) {
					l = l.replaceAll("^\\s*", "").replaceAll("\\s*$", "").toLowerCase();
					lstGeneNames.put(l, l);
				}
				if (l.equals("#Genes_present")) {
					genesZone = true;
				}
			}
			//
			// VeroToxin
			//
			JSONObject dictVeroToxins = jopatho.getJO("dictVeroToxins");
			for (String gene : lstGeneNames.keyNames()) {
				String key = jopatho.getJO("dictVeroToxinsLookup").getS(gene.toUpperCase());
				if (dictVeroToxins.has(key)) {
					dictVeroToxins.put(key, true);
				}
			}
			dictPredictedPathotype.put("VeroToxin", dictVeroToxins);
			//
			// AdhesionGenes
			//
			JSONObject dictAdhesionGenes = jopatho.getJO("dictAdhesionGenes");
			for (String gene : lstGeneNames.keyNames()) {
				String key = jopatho.getJO("dictAdhesionGeneLookup").getS(gene.toUpperCase());
				if (dictAdhesionGenes.has(key)) {
					dictAdhesionGenes.put(key, true);
				}
			}
			dictPredictedPathotype.put("AdhesionGenes", dictAdhesionGenes);
			//
			// GeneList
			//
			file = dir + "rematchModule_report.txt";
			lines = Str.readFileStringArray(file);
			JSONArray jaLstGenes = new JSONArray();
			for (String l : lines) {
				String[] f = l.split("\t");
				String gene = f[0].replaceAll("^\\s*", "").replaceAll("\\s*$", "").toLowerCase();

				if (lstGeneNames.getS(gene).isEmpty()) {
					continue;
				}
				jaLstGenes.put(new JSONObject()
						.put("GeneName", f[0])
						.put("Identity", Float.valueOf(f[1]))
						.put("Coverage", Float.valueOf(f[5])));
			}
			dictPredictedPathotype.put("GeneList", jaLstGenes);
			//
			// PredictedPathotype
			//
			joParamCode.put("PredictedPathotype", dictPredictedPathotype);
		} catch (Exception e) {
			ctx.lg.error("_pathoTyping: dir:patho_typing empty ", e);
			System.exit(1);
		}
	}

	////////////////////////////////////////////////////////////////////////

	/**
	 * DS1-DT1-raw123_spades_scaffolds_L200.fasta
	 * lmonocytogenes 155 abcZ(7) bglA(10) cat(16) dapE(7) dat(5) ldh(2) lhkA(1)
	 *
	 * @param file MLST Tseeman file
	 */
	public static boolean checkMlstSeeman(String file) {
		JSONObject jo = ctx.joSubmission;
		try {
			if (!Str.fileExists(file)) {
				ctx.lg.error("checkMlstSeeman: file does not exists:" + "'" + file + "'");
				return false;
			}
			JSONObject joVal = new JSONObject(
					"cmp,spe,ST,AL1,AL2,AL3,AL4,AL5,AL6,AL7",
					Str.readFileString(file).split("\t"));

			String f = joVal.getS("ST");
			jo.putPath("payload.result.Results.ParamCode.MLSTSequenceType.ST", f);
			JSONArray ja = new JSONArray();
			JSONObject joAl = new JSONObject();
			// System.out.println(joVal.toString(3));
			for (int i = 1; i <= 7; i++) {
				Str al = new Str(joVal.getS("AL" + i));
				if (al.matches("^(.*?)\\((.*?)\\)\r?\n?$")) {
					joAl.put(al.$i(1), al.$i(2));
				} else {
					ctx.lg.error("checkMlstSeeman: allele error: '" +
							al.get() + "' in file " + file);
					System.exit(1);
				}
			}
			ja.put(joAl);

			jo.putPath("payload.result.Results.ParamCode.MLSTSequenceType.GeneList", ja);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}// =====================================================================

/**
 * 
 */
class CgMlstMgr {
	static EfsaOhWgsContext ctx = new EfsaOhWgsContext();
	static String file;
	static String cgmlstschema;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (isCrc32Necessary() && checkFiles()) {
			writeCrc32file();
		}
	}

	/**
	 * 
	 */
	public static boolean isCrc32Necessary() {
		return (ctx.joInput.getS("cfg:crc32transform").equals("Y"));
	}

	/**
	 * 
	 */
	public static boolean checkFiles() {

		file = ctx.joInput.getS("file:alleles");
		cgmlstschema = ctx.joInput.getS("cfg:cgmlstschema");

		if (!Str.fileExists(file)) {
			ctx.lg.error("isCrc32Necessary: file:alleles does not exist" + file);
			return false;
		}
		if (!Str.isDirectory(cgmlstschema)) {
			ctx.lg.error("isCrc32Necessary: cfg:cgmlstschema is not a directory" + cgmlstschema);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param s
	 */
	private static void writeCrc32file() {
		JSONArray ja;
		JSONObject joAlleles;
		JSONObject joCrc32Alleles = new JSONObject();
		String ntAllele, alCode, profileCrc32, fileCrc32;

		try {
			ja = JSONArray.jsonArrayFromCSVFile(file, "\t");
			joAlleles = ja.getJSONObject(0);

			for (String locus : joAlleles.keyNames()) {
				alCode = joAlleles.getS(locus);
				if (locus.matches(".*FILE.*")) {
					joCrc32Alleles.put(locus, alCode);
					continue;
				}
				ntAllele = _readAlleleSequenceFromSchema(locus, alCode);
				joCrc32Alleles.put(locus, _getCRC32(ntAllele));
			}
			ctx.allelicProfileCrc32 = joCrc32Alleles.toCSV("\t");
			// fileCrc32 = file + ".crc32.tsv";
			// Str.writeToFile(fileCrc32, ctx.allelicProfileCrc32);
		} catch (Exception e) {
			ctx.lg.error("writeCrc32file: problem with file:alleles" + file, e);
		}
	}

	/**
	 * return crc32 of input string, 0 in case of empty string according to EFSA
	 * convention
	 * 
	 * @param input string
	 * @return crc32 of input
	 */
	public static long _getCRC32(String input) {
		byte[] bytes = input.getBytes();
		Checksum checksum = new CRC32(); // java.util.zip.CRC32
		checksum.update(bytes, 0, bytes.length);
		long crc32 = checksum.getValue();
		// System.out.println(crc32);
		return crc32;
	}

	/**
	 * return Allele Sequence looking for the allele code in the locus file of
	 * cgMLST schema directory.
	 * Allele codes can be an Integer (NUMBER) or contains the following
	 * possibile values: EXC,INF,PLOT3,PLOT5,LOTSC,NIPH,NIPHEM,ALM,ASM,LNF according
	 * to https://chewbbaca.readthedocs.io/en/latest/user/modules/AlleleCall.html
	 * in case of NUMBER or INF-*NUMBER, the NUMBER value is checked
	 * otherwise return empty string,
	 * 
	 * @param locus  locus filename
	 * @param alCode allele code
	 * @return the Allele Sequence associated of allele code
	 * @throws IOException
	 */
	private static String _readAlleleSequenceFromSchema(String locus, String alCode) throws IOException {
		if (alCode == null) {
			return "";
		}
		if (alCode.toUpperCase().matches("^INF.*")) {
			alCode = alCode.replaceAll("[^0-9]", "");
		} else if (!Str.isInteger(alCode)) {
			return "";
		}

		String locusPath = cgmlstschema + "/" + locus;
		// System.err.println("locusFile: " + locusPath);
		String locusName = locus.replaceAll(".fasta", "");

		String alHeader = locusName + "_" + alCode;
		String alValue = "";

		Str locusString = new Str(Str.readFileString(locusPath));
		String alMatch = ".*>" + alHeader + "\r?\n([ATCG].*?[ATCG])\r?\n>.*";
		if (locusString.matches(alMatch)) {
			alValue = locusString.$i(1).replaceAll("\n", "").replaceAll("\r", "");
			// System.out.println(alValue);
		}
		return alValue;
	}

}// =====================================================================
