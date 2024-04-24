/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.authorization.method.aspectj;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Luke Taylor
 * @since 3.0.3
 */
public class PreAuthorizeAspectTests {

	private final TestingAuthenticationToken anne = new TestingAuthenticationToken("anne", "", "ROLE_A");

	private MethodInterceptor interceptor;

	private final SecuredImpl secured = new SecuredImpl();

	private final SecuredImplSubclass securedSub = new SecuredImplSubclass();

	private final PrePostSecured prePostSecured = new PrePostSecured();

	@BeforeEach
	public final void setUp() {
		MockitoAnnotations.initMocks(this);
		this.interceptor = AuthorizationManagerBeforeMethodInterceptor.preAuthorize();
		PreAuthorizeAspect secAspect = PreAuthorizeAspect.aspectOf();
		secAspect.setSecurityInterceptor(this.interceptor);
	}

	@AfterEach
	public void clearContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void securedInterfaceMethodAllowsAllAccess() {
		this.secured.securedMethod();
	}

	@Test
	public void securedClassMethodDeniesUnauthenticatedAccess() {
		assertThatExceptionOfType(AuthenticationCredentialsNotFoundException.class)
			.isThrownBy(() -> this.secured.securedClassMethod());
	}

	@Test
	public void securedClassMethodAllowsAccessToRoleA() {
		SecurityContextHolder.getContext().setAuthentication(this.anne);
		this.secured.securedClassMethod();
	}

	@Test
	public void internalPrivateCallIsIntercepted() {
		SecurityContextHolder.getContext().setAuthentication(this.anne);
		assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(() -> this.secured.publicCallsPrivate());
		assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(() -> this.securedSub.publicCallsPrivate());
	}

	@Test
	public void protectedMethodIsIntercepted() {
		SecurityContextHolder.getContext().setAuthentication(this.anne);
		assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(() -> this.secured.protectedMethod());
	}

	@Test
	public void overriddenProtectedMethodIsNotIntercepted() {
		// AspectJ doesn't inherit annotations
		this.securedSub.protectedMethod();
	}

	// SEC-1262
	@Test
	public void denyAllPreAuthorizeDeniesAccess() {
		SecurityContextHolder.getContext().setAuthentication(this.anne);
		assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(this.prePostSecured::denyAllMethod);
	}

	interface SecuredInterface {

		@PreAuthorize("hasRole('X')")
		void securedMethod();

	}

	static class SecuredImpl implements SecuredInterface {

		// Not really secured because AspectJ doesn't inherit annotations from interfaces
		@Override
		public void securedMethod() {
		}

		@PreAuthorize("hasRole('A')")
		void securedClassMethod() {
		}

		@PreAuthorize("hasRole('X')")
		private void privateMethod() {
		}

		@PreAuthorize("hasRole('X')")
		protected void protectedMethod() {
		}

		@PreAuthorize("hasRole('X')")
		void publicCallsPrivate() {
			privateMethod();
		}

	}

	static class SecuredImplSubclass extends SecuredImpl {

		@Override
		protected void protectedMethod() {
		}

		@Override
		public void publicCallsPrivate() {
			super.publicCallsPrivate();
		}

	}

	static class PrePostSecured {

		@PreAuthorize("denyAll")
		void denyAllMethod() {
		}

	}

}
