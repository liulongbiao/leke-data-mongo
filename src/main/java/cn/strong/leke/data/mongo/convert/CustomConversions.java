/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.util.Assert;

import cn.strong.leke.data.mongo.convert.BsonConverters.BigDecimalToStringConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.BigIntegerToStringConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.StringToBigDecimalConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.StringToBigIntegerConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.StringToURLConverter;
import cn.strong.leke.data.mongo.convert.BsonConverters.URLToStringConverter;

/**
 * 自定义类型转换注册信息
 * 
 * @author liulongbiao
 *
 */
public class CustomConversions {

	private static final Logger LOG = LoggerFactory.getLogger(CustomConversions.class);
	private static final String READ_CONVERTER_NOT_SIMPLE = "Registering converter from %s to %s as reading converter although it doesn't convert from a Mongo supported type! You might wanna check you annotation setup at the converter implementation.";
	private static final String WRITE_CONVERTER_NOT_SIMPLE = "Registering converter from %s to %s as writing converter although it doesn't convert to a Mongo supported type! You might wanna check you annotation setup at the converter implementation.";

	private final Set<ConvertiblePair> readingPairs;
	private final Set<ConvertiblePair> writingPairs;

	private final List<Object> converters;

	private final Map<ConvertiblePair, Optional<Class<?>>> customReadTargetTypes;
	private final Map<ConvertiblePair, Optional<Class<?>>> customWriteTargetTypes;
	private final Map<Class<?>, Optional<Class<?>>> rawWriteTargetTypes;

	/**
	 * 创建空 {@link CustomConversions} 对象
	 */
	CustomConversions() {
		this(new ArrayList<Object>());
	}

	/**
	 * 创建 {@link CustomConversions} 实例，并注册给定转换器
	 * 
	 * @param converters
	 */
	public CustomConversions(List<?> converters) {

		Assert.notNull(converters);

		this.readingPairs = new LinkedHashSet<ConvertiblePair>();
		this.writingPairs = new LinkedHashSet<ConvertiblePair>();
		this.customReadTargetTypes = new ConcurrentHashMap<ConvertiblePair, Optional<Class<?>>>();
		this.customWriteTargetTypes = new ConcurrentHashMap<ConvertiblePair, Optional<Class<?>>>();
		this.rawWriteTargetTypes = new ConcurrentHashMap<Class<?>, Optional<Class<?>>>();

		List<Object> toRegister = new ArrayList<Object>();

		// Add user provided converters to make sure they can override the defaults
		toRegister.addAll(converters);
		toRegister.add(CustomToStringConverter.INSTANCE);
		toRegister.add(BigDecimalToStringConverter.INSTANCE);
		toRegister.add(StringToBigDecimalConverter.INSTANCE);
		toRegister.add(BigIntegerToStringConverter.INSTANCE);
		toRegister.add(StringToBigIntegerConverter.INSTANCE);
		toRegister.add(URLToStringConverter.INSTANCE);
		toRegister.add(StringToURLConverter.INSTANCE);

		for (Object c : toRegister) {
			registerConversion(c);
		}

		Collections.reverse(toRegister);

		this.converters = Collections.unmodifiableList(toRegister);
	}

	/**
	 * 是否是简单类型
	 * 
	 * @param type
	 * @return
	 */
	public boolean isSimpleType(Class<?> type) {
		return BsonSimpleTypes.isSimpleType(type);
	}

	/**
	 * 使用注册的转换器填充给定的 {@link GenericConversionService}
	 * 
	 * @param conversionService
	 */
	public void registerConvertersIn(GenericConversionService conversionService) {

		for (Object converter : converters) {

			boolean added = false;

			if (converter instanceof Converter) {
				conversionService.addConverter((Converter<?, ?>) converter);
				added = true;
			}

			if (converter instanceof ConverterFactory) {
				conversionService.addConverterFactory((ConverterFactory<?, ?>) converter);
				added = true;
			}

			if (converter instanceof GenericConverter) {
				conversionService.addConverter((GenericConverter) converter);
				added = true;
			}

			if (!added) {
				throw new IllegalArgumentException(
						"Given set contains element that is neither Converter nor ConverterFactory!");
			}
		}
	}

	/**
	 * 注册给定转换器的注册类型对
	 * 
	 * @param converter
	 */
	private void registerConversion(Object converter) {

		Class<?> type = converter.getClass();
		boolean isWriting = type.isAnnotationPresent(WritingConverter.class);
		boolean isReading = type.isAnnotationPresent(ReadingConverter.class);

		if (converter instanceof GenericConverter) {
			GenericConverter genericConverter = (GenericConverter) converter;
			for (ConvertiblePair pair : genericConverter.getConvertibleTypes()) {
				register(new ConverterRegistration(pair, isReading, isWriting));
			}
		} else if (converter instanceof Converter) {
			Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(converter.getClass(),
					Converter.class);
			register(new ConverterRegistration(arguments[0], arguments[1], isReading, isWriting));
		} else {
			throw new IllegalArgumentException("Unsupported Converter type!");
		}
	}

	/**
	 * 注册给定的 {@link ConvertiblePair} 作为读取或写入类型对
	 * 
	 * @param converterRegistration
	 */
	private void register(ConverterRegistration converterRegistration) {

		ConvertiblePair pair = converterRegistration.getConvertiblePair();

		if (converterRegistration.isReading()) {

			readingPairs.add(pair);

			if (LOG.isWarnEnabled() && !converterRegistration.isSimpleSourceType()) {
				LOG.warn(String.format(READ_CONVERTER_NOT_SIMPLE, pair.getSourceType(),
						pair.getTargetType()));
			}
		}

		if (converterRegistration.isWriting()) {

			writingPairs.add(pair);

			if (LOG.isWarnEnabled() && !converterRegistration.isSimpleTargetType()) {
				LOG.warn(String.format(WRITE_CONVERTER_NOT_SIMPLE, pair.getSourceType(),
						pair.getTargetType()));
			}
		}
	}

	/**
	 * 返回应转换成的给定目标类型，当注册了自定义转换器将其转换成 BSON 类型的时候
	 * 
	 * @param sourceType
	 *            不能为 {@literal null}
	 * @return
	 */
	public Class<?> getCustomWriteTarget(final Class<?> sourceType) {
		return getOrCreateAndCache(sourceType, rawWriteTargetTypes, () -> {
				return getCustomTarget(sourceType, null, writingPairs);
		});
	}

	/**
	 * 返回当我们可以指定一个目标类型时应写出的类型。该类型应该是给定类型的子类。 若 {@code expectedTargetType} 为
	 * {@literal null} 我们简单的返回第一个匹配目标类型或没有匹配类型时返回 {@literal null}
	 * 
	 * @param sourceType
	 *            不能为 {@literal null}
	 * @param requestedTargetType
	 * @return
	 */
	public Class<?> getCustomWriteTarget(final Class<?> sourceType,
			final Class<?> requestedTargetType) {

		if (requestedTargetType == null) {
			return getCustomWriteTarget(sourceType);
		}

		return getOrCreateAndCache(new ConvertiblePair(sourceType, requestedTargetType),
				customWriteTargetTypes, () -> {
					return getCustomTarget(sourceType, requestedTargetType, writingPairs);
				});
	}

	/**
	 * 是否存在自定义转换将目标写成 BSON 简单类型
	 * 
	 * @param sourceType
	 *            不能为 {@literal null}
	 * @return
	 */
	public boolean hasCustomWriteTarget(Class<?> sourceType) {
		return hasCustomWriteTarget(sourceType, null);
	}

	/**
	 * 是否存在自定义转换将目标写成给定目标类型
	 * 
	 * @param sourceType
	 *            不能为 {@literal null}.
	 * @param requestedTargetType
	 * @return
	 */
	public boolean hasCustomWriteTarget(Class<?> sourceType, Class<?> requestedTargetType) {
		return getCustomWriteTarget(sourceType, requestedTargetType) != null;
	}

	/**
	 * 返回是否存在自定义转换将源类型读取为给定目标类型
	 * 
	 * @param sourceType
	 *            不能为 {@literal null}
	 * @param requestedTargetType
	 *            不能为 {@literal null}
	 * @return
	 */
	public boolean hasCustomReadTarget(Class<?> sourceType, Class<?> requestedTargetType) {
		return getCustomReadTarget(sourceType, requestedTargetType) != null;
	}

	/**
	 * Returns the actual target type for the given {@code sourceType} and
	 * {@code requestedTargetType}. Note that the returned {@link Class} could
	 * be an assignable type to the given {@code requestedTargetType}.
	 * 
	 * @param sourceType
	 *            不能为 {@literal null}.
	 * @param requestedTargetType
	 *            can be {@literal null}.
	 * @return
	 */
	private Class<?> getCustomReadTarget(final Class<?> sourceType,
			final Class<?> requestedTargetType) {

		if (requestedTargetType == null) {
			return null;
		}

		return getOrCreateAndCache(new ConvertiblePair(sourceType, requestedTargetType),
				customReadTargetTypes, () -> {
						return getCustomTarget(sourceType, requestedTargetType, readingPairs);
				});
	}

	/**
	 * Inspects the given {@link ConvertiblePair}s for ones that have a source
	 * compatible type as source. Additionally checks assignability of the
	 * target type if one is given.
	 * 
	 * @param sourceType
	 *            不能为 {@literal null}.
	 * @param requestedTargetType
	 *            可以为 {@literal null}.
	 * @param pairs
	 *            不能为 {@literal null}.
	 * @return
	 */
	private static Class<?> getCustomTarget(Class<?> sourceType, Class<?> requestedTargetType,
			Collection<ConvertiblePair> pairs) {

		Assert.notNull(sourceType);
		Assert.notNull(pairs);

		if (requestedTargetType != null
				&& pairs.contains(new ConvertiblePair(sourceType, requestedTargetType))) {
			return requestedTargetType;
		}

		for (ConvertiblePair typePair : pairs) {
			if (typePair.getSourceType().isAssignableFrom(sourceType)) {
				Class<?> targetType = typePair.getTargetType();
				if (requestedTargetType == null || targetType.isAssignableFrom(requestedTargetType)) {
					return targetType;
				}
			}
		}

		return null;
	}

	private static <T> Class<?> getOrCreateAndCache(T key, Map<T, Optional<Class<?>>> cache,
			Supplier<Class<?>> supplier) {
		return cache.computeIfAbsent(key, k -> {
			return Optional.ofNullable(supplier.get());
		}).orElse(null);
	}

	@WritingConverter
	private enum CustomToStringConverter implements GenericConverter {

		INSTANCE;

		public Set<ConvertiblePair> getConvertibleTypes() {

			ConvertiblePair localeToString = new ConvertiblePair(Locale.class, String.class);
			ConvertiblePair charToString = new ConvertiblePair(Character.class, String.class);

			return new HashSet<ConvertiblePair>(Arrays.asList(localeToString, charToString));
		}

		public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
			return source.toString();
		}
	}
}
