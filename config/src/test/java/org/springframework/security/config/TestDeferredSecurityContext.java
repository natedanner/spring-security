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

package org.springframework.security.config;

import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;

/**
 * @author Steve Riesenberg
 */
public class TestDeferredSecurityContext implements DeferredSecurityContext {

	private final SecurityContext securityContext;

	private final boolean isGenerated;

	public TestDeferredSecurityContext(SecurityContext securityContext, boolean isGenerated) {
		this.securityContext = securityContext;
		this.isGenerated = isGenerated;
	}

	@Override
	public SecurityContext get() {
		return this.securityContext;
	}

	@Override
	public boolean isGenerated() {
		return this.isGenerated;
	}

}
