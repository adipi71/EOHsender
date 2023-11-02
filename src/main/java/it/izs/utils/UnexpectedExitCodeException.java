package it.izs.utils;

public class UnexpectedExitCodeException extends Exception {
    
    private ShellExecutionStatus executionStatus;

    public UnexpectedExitCodeException(String message, ShellExecutionStatus executionStatus) {
        super(message);
        this.executionStatus = executionStatus;
    }

    public ShellExecutionStatus getExecutionStatus() {
        return executionStatus;
    }
}
