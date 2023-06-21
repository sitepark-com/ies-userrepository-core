package com.sitepark.ies.userrepository.core.domain.exception;

import com.sitepark.ies.userrepository.core.domain.entity.Anchor;

public class AnchorNotFound extends UserRepositoryException {

	private static final long serialVersionUID = 1L;

	private final Anchor anchor;

	public AnchorNotFound(Anchor anchor) {
		super();
		this.anchor = anchor;
	}

	public Anchor getAnchor() {
		return this.anchor;
	}

	@Override
	public String getMessage() {
		return "User with anchor " + this.anchor + " not found";
	}
}