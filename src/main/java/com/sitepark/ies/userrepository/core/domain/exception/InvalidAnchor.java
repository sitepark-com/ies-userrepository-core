package com.sitepark.ies.userrepository.core.domain.exception;

public class InvalidAnchor extends UserRepositoryException {

	private final String name;

	private static final long serialVersionUID = 1L;

	public InvalidAnchor(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String getMessage() {
		return "Invalid anchor '" + this.name + ": " + super.getMessage();
	}
}
