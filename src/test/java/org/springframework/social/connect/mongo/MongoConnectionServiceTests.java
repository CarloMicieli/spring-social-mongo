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
package org.springframework.social.connect.mongo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.test.FakeConnection;
import org.springframework.social.test.FakeConnectionFactory;
import org.springframework.social.test.FakeProvider;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.*;

/**
 * The test class for the Mongodb connection service.
 * 
 * @author Carlo P. Micieli
 */
public class MongoConnectionServiceTests extends SpringTest {

	private @Autowired MongoTemplate mongoOps;
	private @Autowired MongoConnectionService service;
	
	private FakeConnectionFactory<FakeProvider> factory = 
			new FakeConnectionFactory<>("fake", null, null);
	
	private MongoConnection create(String userId, 
			String providerId, 
			String providerUserId, 
			String displayName,
			int rank) {
		
		MongoConnection c = new MongoConnection();
		c.setUserId(userId);
		c.setDisplayName(displayName);
		c.setProviderId(providerId);
		c.setProviderUserId(providerUserId);
		c.setRank(rank);
		return c;
	}
	
	@Before
	public void setup() {
		List<MongoConnection> cnns = Arrays.asList(
			create("joey", "twitter", "@JeffreyHyman", "joey r.", 2),
			create("joey", "twitter", "@joey_ramones", "joey r.", 1),
			create("joey", "facebook", "joey.ramones", "joey r.", 1),
			create("johnny", "facebook", "JohnnyRamones", "johnny r.", 1),
			create("tommy", "twitter", "@joey_ramones", "joey r.", 1),
			create("cj", "fake", "c-j", "cj", 1)
				);

		mongoOps.insert(cnns, MongoConnection.class);
	}

	@After
	public void tearDown() {
		mongoOps.remove(new Query(), MongoConnection.class);
	}
	
	@Test
	public void shouldReturnMultipleConnections() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.put("twitter", Arrays.asList("@JeffreyHyman", "@joey_ramones"));
		map.put("facebook", Arrays.asList("joey.ramones"));
		
		List<Connection<?>> connections = service.getConnections("joey", map);
		assertEquals(3, connections.size());
		assertEquals("[{facebook, joey.ramones, joey r.}, {twitter, @joey_ramones, joey r.}, {twitter, @JeffreyHyman, joey r.}]", 
				connections.toString());
	}
	
	@Test
	public void shouldReturnTheUserIds() {
		List<String> userIds = service.getUserIds("twitter", "@joey_ramones");
		assertNotNull(userIds);
		assertEquals(2, userIds.size());
		assertEquals("[joey, tommy]", userIds.toString());
	}
		
	@Test
	public void shouldReturnTheSetOfUserIds() {
		Set<String> providedIds = new HashSet<String>();
		providedIds.add("joey.ramones");
		providedIds.add("JohnnyRamones");
		
		Set<String> userIds = service.getUserIds("facebook", providedIds);
		assertNotNull(userIds);
		assertEquals(2, userIds.size());
		assertEquals("[joey, johnny]", userIds.toString());
	}
	
	@Test
	public void shouldReturnTheDefaultRank() {
		int rank = service.getMaxRank("deedee", "twitter");
		assertEquals(1, rank);
	}
		
	@Test
	public void shouldReturnTheMaxRankForAProvider() {
		int rank = service.getMaxRank("joey", "twitter");
		assertEquals(3, rank);
	}
	
	@Test
	public void shouldReturnNullIfTheConnectionIsNotFound() {
		Connection<?> conn = service.getConnection("a", "b", "c");
		assertNull(conn);
	}
	
	@Test
	public void shouldFindPrimaryConnection() {
		Connection<?> conn = service.getPrimaryConnection("joey", "twitter");
		assertNotNull("Connection not found", conn);
		assertEquals("twitter", conn.getKey().getProviderId());
		assertEquals("@joey_ramones", conn.getKey().getProviderUserId());
	}
	
	@Test
	public void shouldFindConnection() {
		Connection<?> conn = service.getConnection("joey", "facebook", "joey.ramones");
		assertNotNull("Connection not found", conn);
		assertEquals("facebook", conn.getKey().getProviderId());
		assertEquals("joey.ramones", conn.getKey().getProviderUserId());
	}
	
	@Test
	public void shouldListTheConnectionsForUserAndProviderSortByRank() {
		List<Connection<?>> connections =
				service.getConnections("joey", "twitter");
		
		assertEquals(2, connections.size());
		assertEquals("[{twitter, @joey_ramones, joey r.}, {twitter, @JeffreyHyman, joey r.}]", 
				connections.toString());
	}
	
	@Test
	public void shouldListTheConnectionsForUserSortByProviderAndRank() {
		List<Connection<?>> connections =
				service.getConnections("joey");
		
		assertEquals(3, connections.size());
		assertEquals("[{facebook, joey.ramones, joey r.}, {twitter, @joey_ramones, joey r.}, {twitter, @JeffreyHyman, joey r.}]", 
				connections.toString());
	}
	
	@Test
	public void shouldCreateNewConnection() {
		Connection<?> userConn = factory.createConnection("userName", "user name");
		service.create("UserId", userConn, 5);
		List<Connection<?>> connections =
				service.getConnections("UserId", "fake");
		
		assertEquals(1, connections.size());
		
		FakeConnection<?> conn = (FakeConnection<?>) connections.get(0);
		assertEquals("fake", conn.getData().getProviderId());
		assertEquals("userName", conn.getData().getProviderUserId());
		assertEquals("user name", conn.getData().getDisplayName());
	}
	
	@Test(expected = DuplicateKeyException.class)
	public void shouldThrowExceptionIfDuplicatedValues() {
		Connection<?> userConn = factory.createConnection("cj", "cj");
		service.create("cj", userConn, 1);
	}
	
	@Test
	public void shouldUpdateTheConnection() {
		//Connection<?> userConn = factory.createConnection("cj", "cj");
		
		Connection<?> conn = service.getConnection("joey", "twitter", "@JeffreyHyman");
		assertEquals("joey r.", conn.getDisplayName());
	
		service.update("joey", conn);
		
		Connection<?> conn2 = service.getConnection("joey", "twitter", "@JeffreyHyman");
		assertEquals("joey r.", conn2.getDisplayName());
	}
	
	@Test
	public void shouldRemoveTheConnection() {
		service.remove("joey", new ConnectionKey("twitter", "@JeffreyHyman"));
		
		Connection<?> conn = service.getConnection("joey", "twitter", "@JeffreyHyman");
		assertNull("Connection not removed", conn);
	}
	
	@Test
	public void shouldRemoveTheConnectionForAProvider() {
		service.remove("joey", "twitter");
		
		List<Connection<?>> conn = service.getConnections("joey", "twitter");
		assertEquals(0, conn.size());
	}
}
