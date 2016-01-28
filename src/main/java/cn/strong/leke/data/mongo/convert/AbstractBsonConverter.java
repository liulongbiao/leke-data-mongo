/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.math.BigInteger;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import cn.strong.leke.data.mongo.convert.BsonConverters.BigIntegerToObjectIdConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.ObjectIdToBigIntegerConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.ObjectIdToStringConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.StringToObjectIdConverter;

/**
 * 抽象 BsonConverter 实现
 * 
 * @author liulongbiao
 *
 */
public abstract class AbstractBsonConverter implements BsonConverter, InitializingBean {
	protected final GenericConversionService conversionService;
	protected CustomConversions conversions = new CustomConversions();

	public AbstractBsonConverter(GenericConversionService conversionService) {
		this.conversionService = conversionService == null ? new DefaultConversionService()
				: conversionService;
	}

	public void setCustomConversions(CustomConversions conversions) {
		this.conversions = conversions;
	}

	private void initializeConverters() {
		putIfAbsent(ObjectId.class, String.class, ObjectIdToStringConverter.INSTANCE);
		putIfAbsent(String.class, ObjectId.class, StringToObjectIdConverter.INSTANCE);
		putIfAbsent(ObjectId.class, BigInteger.class, ObjectIdToBigIntegerConverter.INSTANCE);
		putIfAbsent(BigInteger.class, ObjectId.class, BigIntegerToObjectIdConverter.INSTANCE);

		conversions.registerConvertersIn(conversionService);
	}

	private <S, T> void putIfAbsent(Class<S> sourceType, Class<T> targetType,
			Converter<S, T> converter) {
		if (!conversionService.canConvert(sourceType, targetType)) {
			conversionService.addConverter(converter);
		}
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void afterPropertiesSet() {
		initializeConverters();
	}
}
