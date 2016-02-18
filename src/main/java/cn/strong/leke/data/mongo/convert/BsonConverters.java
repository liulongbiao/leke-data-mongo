/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;

import org.bson.types.ObjectId;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * BSON 转换器
 * 
 * @author liulongbiao
 *
 */
public class BsonConverters {

	private BsonConverters() {
		super();
	}

	/**
	 * Simple singleton to convert {@link ObjectId}s to their {@link String}
	 * representation.
	 * 
	 * @author Oliver Gierke
	 */
	public static enum ObjectIdToStringConverter implements Converter<ObjectId, String> {
		INSTANCE;

		public String convert(ObjectId id) {
			return id == null ? null : id.toString();
		}
	}

	/**
	 * Simple singleton to convert {@link String}s to their {@link ObjectId}
	 * representation.
	 * 
	 * @author Oliver Gierke
	 */
	public static enum StringToObjectIdConverter implements Converter<String, ObjectId> {
		INSTANCE;

		public ObjectId convert(String source) {
			return StringUtils.hasText(source) ? new ObjectId(source) : null;
		}
	}

	/**
	 * Simple singleton to convert {@link ObjectId}s to their
	 * {@link java.math.BigInteger} representation.
	 * 
	 * @author Oliver Gierke
	 */
	public static enum ObjectIdToBigIntegerConverter implements Converter<ObjectId, BigInteger> {
		INSTANCE;

		public BigInteger convert(ObjectId source) {
			return source == null ? null : new BigInteger(source.toString(), 16);
		}
	}

	/**
	 * Simple singleton to convert {@link BigInteger}s to their {@link ObjectId}
	 * representation.
	 * 
	 * @author Oliver Gierke
	 */
	public static enum BigIntegerToObjectIdConverter implements Converter<BigInteger, ObjectId> {
		INSTANCE;

		public ObjectId convert(BigInteger source) {
			return source == null ? null : new ObjectId(source.toString(16));
		}
	}

	public static enum BigDecimalToStringConverter implements Converter<BigDecimal, String> {
		INSTANCE;

		public String convert(BigDecimal source) {
			return source == null ? null : source.toString();
		}
	}

	public static enum StringToBigDecimalConverter implements Converter<String, BigDecimal> {
		INSTANCE;

		public BigDecimal convert(String source) {
			return StringUtils.hasText(source) ? new BigDecimal(source) : null;
		}
	}

	public static enum BigIntegerToStringConverter implements Converter<BigInteger, String> {
		INSTANCE;

		public String convert(BigInteger source) {
			return source == null ? null : source.toString();
		}
	}

	public static enum StringToBigIntegerConverter implements Converter<String, BigInteger> {
		INSTANCE;

		public BigInteger convert(String source) {
			return StringUtils.hasText(source) ? new BigInteger(source) : null;
		}
	}

	public static enum URLToStringConverter implements Converter<URL, String> {
		INSTANCE;

		public String convert(URL source) {
			return source == null ? null : source.toString();
		}
	}

	public static enum StringToURLConverter implements Converter<String, URL> {
		INSTANCE;

		private static final TypeDescriptor SOURCE = TypeDescriptor.valueOf(String.class);
		private static final TypeDescriptor TARGET = TypeDescriptor.valueOf(URL.class);

		public URL convert(String source) {

			try {
				return source == null ? null : new URL(source);
			} catch (MalformedURLException e) {
				throw new ConversionFailedException(SOURCE, TARGET, source, e);
			}
		}
	}
}
