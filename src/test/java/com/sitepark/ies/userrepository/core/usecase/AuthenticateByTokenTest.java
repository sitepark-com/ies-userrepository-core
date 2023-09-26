package com.sitepark.ies.userrepository.core.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.sitepark.ies.userrepository.core.domain.entity.AccessToken;
import com.sitepark.ies.userrepository.core.domain.entity.User;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenExpired;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenNotActive;
import com.sitepark.ies.userrepository.core.domain.exception.AccessTokenRevoked;
import com.sitepark.ies.userrepository.core.domain.exception.InvalidAccessToken;
import com.sitepark.ies.userrepository.core.port.AccessTokenRepository;
import com.sitepark.ies.userrepository.core.port.UserRepository;

class AuthenticateByTokenTest {

	private static final String TOKEN_NAME = "Test Token";

	private static final String TOKEN_STRING = "abc";

	@Test
	void testTokenNotFound() {

		AccessTokenRepository accessTokenRepository = mock();
		when(accessTokenRepository.getByToken(any())).thenReturn(Optional.empty());
		UserRepository userRepository = mock();

		var authenticateByToken = new AuthenticateByToken(
				accessTokenRepository,
				userRepository);

		assertThrows(InvalidAccessToken.class, () -> {
			authenticateByToken.authenticateByToken(TOKEN_STRING);
		});
	}

	@Test
	void testTokenNotActive() {

		AccessToken accessToken = AccessToken.builder()
				.id(1)
				.name(TOKEN_NAME)
				.user(2)
				.active(false)
				.build();

		AccessTokenRepository accessTokenRepository = mock();
		when(accessTokenRepository.getByToken(any())).thenReturn(Optional.of(accessToken));
		UserRepository userRepository = mock();

		var authenticateByToken = new AuthenticateByToken(
				accessTokenRepository,
				userRepository);

		assertThrows(AccessTokenNotActive.class, () -> {
			authenticateByToken.authenticateByToken(TOKEN_STRING);
		});
	}

	@Test
	void testTokenRevoked() {

		AccessToken accessToken = AccessToken.builder()
				.id(1)
				.name(TOKEN_NAME)
				.user(2)
				.revoked(true)
				.build();

		AccessTokenRepository accessTokenRepository = mock();
		when(accessTokenRepository.getByToken(any())).thenReturn(Optional.of(accessToken));
		UserRepository userRepository = mock();

		var authenticateByToken = new AuthenticateByToken(
				accessTokenRepository,
				userRepository);

		assertThrows(AccessTokenRevoked.class, () -> {
			authenticateByToken.authenticateByToken("abc");
		});
	}

	@Test
	void testTokenExpired() {

		OffsetDateTime expiredAt = OffsetDateTime.now().minusDays(1);

		AccessToken accessToken = AccessToken.builder()
				.id(1)
				.name(TOKEN_NAME)
				.user(2)
				.expiresAt(expiredAt)
				.build();

		AccessTokenRepository accessTokenRepository = mock();
		when(accessTokenRepository.getByToken(any())).thenReturn(Optional.of(accessToken));
		UserRepository userRepository = mock();

		var authenticateByToken = new AuthenticateByToken(
				accessTokenRepository,
				userRepository);

		assertThrows(AccessTokenExpired.class, () -> {
			authenticateByToken.authenticateByToken(TOKEN_STRING);
		});
	}

	@Test
	void testUserNotFound() {

		AccessToken accessToken = AccessToken.builder()
				.id(1)
				.name(TOKEN_NAME)
				.user(2)
				.build();

		AccessTokenRepository accessTokenRepository = mock();
		when(accessTokenRepository.getByToken(any())).thenReturn(Optional.of(accessToken));

		UserRepository userRepository = mock();

		when(userRepository.get(anyLong())).thenReturn(Optional.empty());

		var authenticateByToken = new AuthenticateByToken(
				accessTokenRepository,
				userRepository);

		assertThrows(InvalidAccessToken.class, () -> {
			authenticateByToken.authenticateByToken(TOKEN_STRING);
		});
	}

	@Test
	void testValidAutentification() {

		AccessToken accessToken = AccessToken.builder()
				.id(1)
				.name(TOKEN_NAME)
				.user(2)
				.build();

		User user = User.builder()
				.id(1)
				.login("test")
				.build();

		AccessTokenRepository accessTokenRepository = mock();
		when(accessTokenRepository.getByToken(any())).thenReturn(Optional.of(accessToken));

		UserRepository userRepository = mock();

		when(userRepository.get(anyLong())).thenReturn(Optional.of(user));

		var authenticateByToken = new AuthenticateByToken(
				accessTokenRepository,
				userRepository);

		User authenticatedUser = authenticateByToken.authenticateByToken(TOKEN_STRING);
		assertEquals(user.getId().get(), authenticatedUser.getId().get(), "unexpected user");
	}

	@Test
	void testValidAutentificationWithExpiredDate() {

		OffsetDateTime expiredAt = OffsetDateTime.now().plusDays(1);

		AccessToken accessToken = AccessToken.builder()
				.id(1)
				.name(TOKEN_NAME)
				.expiresAt(expiredAt)
				.user(2)
				.build();

		User user = User.builder()
				.id(1)
				.login("test")
				.build();

		AccessTokenRepository accessTokenRepository = mock();
		when(accessTokenRepository.getByToken(any())).thenReturn(Optional.of(accessToken));

		UserRepository userRepository = mock();

		when(userRepository.get(anyLong())).thenReturn(Optional.of(user));

		var authenticateByToken = new AuthenticateByToken(
				accessTokenRepository,
				userRepository);

		User authenticatedUser = authenticateByToken.authenticateByToken(TOKEN_STRING);
		assertEquals(user.getId().get(), authenticatedUser.getId().get(), "unexpected user");
	}

}