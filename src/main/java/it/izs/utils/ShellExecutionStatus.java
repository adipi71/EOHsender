package it.izs.utils;

public class ShellExecutionStatus {
    
    private int exitCode;
    private String stdOut = "";
    private String stdError = "";

    public ShellExecutionStatus(int exitCode, String stdOut, String stdError) {
        this.exitCode = exitCode;
        this.stdOut = stdOut;
        this.stdError = stdError;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getStdError() {
        return stdError;
    }

    public String getStdOut() {
        return stdOut;
    }

    public String getOut() {
        return (stdOut + " " + stdError).trim();
    }

}