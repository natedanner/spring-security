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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.authorization.method.PostFilterAuthorizationMethodInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Luke Taylor
 * @since 3.0.3
 */
public class PostFilterAspectTests {

	private MethodInterceptor interceptor;

	private final PrePostSecured prePostSecured = new PrePostSecured();

	@BeforeEach
	public final void setUp() {
		MockitoAnnotations.initMocks(this);
		this.interceptor = new PostFilterAuthorizationMethodInterceptor();
		PostFilterAspect secAspect = PostFilterAspect.aspectOf();
		secAspect.setSecurityInterceptor(this.interceptor);
	}

	@Test
	public void preFilterMethodWhenListThenFilters() {
		List<String> objects = new ArrayList<>(Arrays.asList("apple", "banana", "aubergine", "orange"));
		assertThat(this.prePostSecured.postFilterMethod(objects)).containsExactly("apple", "aubergine");
	}

	static class PrePostSecured {

		@PostFilter("filterObject.startsWith('a')")
		List<String> postFilterMethod(List<String> objects) {
			return objects;
		}

	}

}
