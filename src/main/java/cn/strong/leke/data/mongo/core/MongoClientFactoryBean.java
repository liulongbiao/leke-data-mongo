/**
 * 
 */
package cn.strong.leke.data.mongo.core;

import static com.mongodb.MongoClientOptions.builder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import cn.strong.leke.data.mongo.codec.BeanCodecProvider;

/**
 * MongoCleint 工厂Bean
 * 
 * @author liulongbiao
 *
 */
public class MongoClientFactoryBean implements FactoryBean<MongoClient>,
		InitializingBean, DisposableBean {

	private MongoClient client;

	private String replicaset;
	private String credentials;
	private MongoClientOptions options;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<ServerAddress> seeds = buildSeeds();
		List<MongoCredential> credentialsList = buildCredentials();

		// 注册 BeanCodecProvider 为最后一个 Codec
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
				MongoClient.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(new BeanCodecProvider()));
		Builder builder = options == null ? builder() : builder(options);
		MongoClientOptions ops = builder.codecRegistry(codecRegistry).build();

		client = new MongoClient(seeds, credentialsList, ops);
	}

	private List<ServerAddress> buildSeeds() throws UnknownHostException {
		List<ServerAddress> seeds = new ArrayList<ServerAddress>();
		if (StringUtils.hasText(replicaset)) {
			String[] uris = replicaset.split(",");
			for (String uri : uris) {
				seeds.add(new ServerAddress(uri));
			}
		} else {
			seeds.add(new ServerAddress());
		}
		return seeds;
	}

	private List<MongoCredential> buildCredentials() {
		if (!StringUtils.hasText(credentials)) {
			return Collections.emptyList();
		}
		List<MongoCredential> result = new ArrayList<MongoCredential>();
		String[] creds = credentials.split(",");
		for (String cred : creds) {
			if (StringUtils.hasText(cred)) {
				String[] triple = cred.split(":");
				if (triple.length != 3) {
					throw new IllegalArgumentException(
							"credential format must confirm to ${username}:${dbname}:${password} ,but actual received :"
									+ cred);
				}
				result.add(MongoCredential.createCredential(triple[0], triple[1],
						triple[2].toCharArray()));
			}
		}
		return result;
	}

	@Override
	public MongoClient getObject() throws Exception {
		return this.client;
	}

	@Override
	public Class<?> getObjectType() {
		return MongoClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		this.client.close();
	}

	public void setReplicaset(String replicaset) {
		this.replicaset = replicaset;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public void setOptions(MongoClientOptions options) {
		this.options = options;
	}

}
