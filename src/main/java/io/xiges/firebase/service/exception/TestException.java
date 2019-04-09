package io.xiges.firebase.service.exception;

public class TestException extends rs.pscode.pomodorofire.service.exception.AbstractException {

	public TestException(String message, Throwable cause) {
		super(message, "TestException", cause);
	}

	private static final long serialVersionUID = -1451835022162714730L;

}
