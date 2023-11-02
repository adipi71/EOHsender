package it.izs.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellUtils {
	
	private String stdOut="";
	private String stdErr="";

	
	public String getStdOut() {
		return stdOut;
	}

	public String getStdErr() {
		return stdErr;
	}

	public boolean isErrorEmpty() {
		return stdErr.isEmpty();
	}
	public boolean isErrorNotEmpty() {
		return (! stdErr.isEmpty() );
	}

	/**
	 * Lancia il comando via bash ritorna ShellExecutionStatus
	 * @param cmd
	 * @return ShellExecutionStatus 
	 * @throws Exception
	 */
	public ShellExecutionStatus execBashCommand(String cmd) throws Exception {
		String[] _cmd = {
				"/bin/bash",
				"-c",
				cmd
				};
		ShellExecutionStatus executionStatus = execReturningExitStatus(_cmd);	
		if (executionStatus.getExitCode() != 0) {
			throw new UnexpectedExitCodeException(String.format("'%s' returned exit code '%s', stderr: %s ", cmd, executionStatus.getExitCode(), executionStatus.getStdError()), executionStatus);
		}	
		return executionStatus;
	}

	/**
	 * Lancia il comando via bashe e ritorna la stringa risultato
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public String execBashScript(String cmd) throws Exception {
		String[] _cmd = {
				"/bin/bash",
				"-c",
				cmd
				};
		return exec(_cmd);		
	}
	/**
	 * Lancia il comando via bashe e ritorna array di stringhe come risultato
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public String[] execBashAndReturnStringArray(String cmd) throws Exception {
		return execBashScript(cmd).split("\n");
	}
	
	public ShellExecutionStatus execReturningExitStatus(String[] cmd) throws Exception {
		Process proc =  Runtime.getRuntime().exec(cmd);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		// Read the output from the command
		//System.out.println("Here is the standard output of the command:\n");
		stdOut="";
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			stdOut += s + "\n";
		}

		// Read any errors from the attempted command
		stdErr="";
		while ((s = stdError.readLine()) != null) {
			stdErr += s + "\n";
		}
		return new ShellExecutionStatus(proc.waitFor(), stdOut, stdErr);	
	}
	
	public String exec(String[] cmd) throws Exception {
		Runtime rt = Runtime.getRuntime();
	
		Process proc = rt.exec(cmd);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		// Read the output from the command
		//System.out.println("Here is the standard output of the command:\n");
		stdOut="";
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			stdOut += s + "\n";
		}

		// Read any errors from the attempted command
		stdErr="";
		while ((s = stdError.readLine()) != null) {
			stdErr += s + "\n";
		}
		return stdOut;		
	}
	/*
	public String[] execResArray(String[] cmd) throws Exception {
		Runtime rt = Runtime.getRuntime();
	
		Process proc = rt.exec(cmd);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		JSONArray ja=new JSONArray();
		// Read the output from the command
		//System.out.println("Here is the standard output of the command:\n");
		stdOut="";
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			ja.put(s);
		}

		// Read any errors from the attempted command
		stdErr="";
		while ((s = stdError.readLine()) != null) {
			stdErr += s + "\n";
		}
		return ja.toStringArray();		
	}*/
	
	
	
}
