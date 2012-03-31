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

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;

public class FakeConnection<A> implements Connection<A> {

	private ConnectionData data;
	public FakeConnection(ConnectionData data) {
		this.data = data;
	}
	
	public ConnectionKey getKey() {
		return new ConnectionKey(data.getProviderId(), data.getProviderUserId());
	}

	public String getDisplayName() {
		return data.getDisplayName();
	}

	public String getProfileUrl() {
		return data.getProfileUrl();
	}

	public String getImageUrl() {
		return data.getImageUrl();
	}

	public ConnectionData getData() {
		return data;
	}
	
	public void sync() {
	}

	public boolean test() {
		return true;
	}

	public boolean hasExpired() {
		return false;
	}

	public void refresh() {		
	}

	public UserProfile fetchUserProfile() {
		return null;
	}

	public void updateStatus(String message) {		
	}

	public A getApi() {
		return null;
	}
		
	public ConnectionData createData() {
		return data;
	}
	
	@Override
	public String toString() {
		return String.format("{%s, %s, %s}", 
				data.getProviderId(), 
				data.getProviderUserId(),
				data.getDisplayName());
	}
}
