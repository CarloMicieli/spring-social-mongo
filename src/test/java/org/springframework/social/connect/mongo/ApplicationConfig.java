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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.test.FakeConnectionFactoryLocator;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;

/**
 * The application configuration class.
 * 
 * @author Carlo P. Micieli
 */
@Configuration
@ComponentScan(basePackages = "org.springframework.social.connect.mongo")
@PropertySource("classpath:spring/application.properties")
public class ApplicationConfig {
	
	@Autowired
	private Environment env;
	
	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(
				new Mongo(env.getProperty("mongo.hostName"), 
						env.getProperty("mongo.portNumber", Integer.class)), 
						env.getProperty("mongo.databaseName"));
	}
	
	public @Bean MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		mongoTemplate.setWriteConcern(WriteConcern.SAFE);
		return mongoTemplate;
	}
	
	public @Bean ConnectionFactoryLocator connectionFactoryLocator()  {
		return new FakeConnectionFactoryLocator();
	}
	
	public @Bean TextEncryptor textEncryptor() {
		return Encryptors.noOpText();
	}
	
	public @Bean ConnectionConverter connectionConverter() {
		return new ConnectionConverter(
				connectionFactoryLocator(),
				textEncryptor());
	}
}
