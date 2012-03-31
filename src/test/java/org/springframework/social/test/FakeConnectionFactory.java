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

import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;

public class FakeConnectionFactory<A> extends ConnectionFactory<A> {

	private String providerId;
	
	public FakeConnectionFactory(String providerId,
			ServiceProvider<A> serviceProvider, ApiAdapter<A> apiAdapter) {
		super(providerId, serviceProvider, apiAdapter);
		this.providerId = providerId;
	}

	@Override
	public Connection<A> createConnection(ConnectionData data) {
		FakeConnection<A> conn = new FakeConnection<>(data);
		return conn;
	}
	
	public Connection<A> createConnection(String providerUserId, String displayName) {
		ConnectionData data = new ConnectionData(providerId,
				providerUserId, 
				displayName,
				String.format("http://profile/%s", providerUserId),
				String.format("http://image/%s", providerUserId),
				"accessToken", 
				"secret", "", 0L);
		return createConnection(data);
	}
}
