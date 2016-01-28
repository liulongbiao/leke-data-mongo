/**
 * 
 */
package cn.strong.leke.data.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import cn.strong.leke.data.mongo.BsonUtils;

/**
 * JavaBean 模型解编码器
 * 
 * @author liulongbiao
 *
 */
public class BeanCodec<T> implements Codec<T> {

	final CodecRegistry registry;
	final Class<T> clazz;

	public BeanCodec(CodecRegistry registry, Class<T> clazz) {
		super();
		this.registry = registry;
		this.clazz = clazz;
	}

	@Override
	public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
		Document doc = BsonUtils.toBSON(value);
		Codec<Document> delegate = registry.get(Document.class);
		delegate.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<T> getEncoderClass() {
		return clazz;
	}

	@Override
	public T decode(BsonReader reader, DecoderContext decoderContext) {
		Codec<Document> docCodec = registry.get(Document.class);
		Document doc = docCodec.decode(reader, decoderContext);
		return BsonUtils.fromBSON(doc, clazz);
	}

}
