/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.test;

import java.util.Set;

import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;

public class FakeConnectionFactoryLocator implements ConnectionFactoryLocator {

	@Override
	public ConnectionFactory<?> getConnectionFactory(String providerId) {
		return new FakeConnectionFactory<Object>("fake", null, null);
	}

	@Override
	public <A> ConnectionFactory<A> getConnectionFactory(Class<A> apiType) {
		return null;
	}

	@Override
	public Set<String> registeredProviderIds() {
		return null;
	}
}