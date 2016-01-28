/**
 * 
 */
package cn.strong.leke.data.mongo.codec;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import cn.strong.leke.data.mongo.convert.BeanMeta;

/**
 * JavaBean 编码器提供者
 * 
 * @author liulongbiao
 *
 */
public class BeanCodecProvider implements CodecProvider {

	@SuppressWarnings("rawtypes")
	private ConcurrentMap<Class, BeanCodec> codecs = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if (codecs.containsKey(clazz)) {
			return codecs.get(clazz);
		}
		if (isBeanClass(clazz)) {
			BeanCodec<T> codec = new BeanCodec<>(registry, clazz);
			codecs.put(clazz, codec);
			return codec;
		}

		return null;
	}

	private <T> boolean isBeanClass(Class<T> clazz) {
		BeanMeta bm = BeanMeta.from(clazz);
		return bm.isBeanClass();
	}
}
