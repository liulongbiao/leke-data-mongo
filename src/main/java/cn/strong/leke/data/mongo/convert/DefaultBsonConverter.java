/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import cn.strong.leke.data.mongo.annotations.ObjectId;
import cn.strong.leke.data.mongo.annotations._id;

/**
 * 默认 BsonConverter 实现
 * 
 * @author yoyobill
 *
 */
public class DefaultBsonConverter extends AbstractBsonConverter {

	private static final String KEY_ID = "_id";

	public DefaultBsonConverter() {
		this(new DefaultConversionService());
	}

	public DefaultBsonConverter(GenericConversionService conversionService) {
		super(conversionService);
	}

	@Override
	public Object toBSON(Object obj) {
		if (obj == null) {
			return null;
		}

		Class<?> target = conversions.getCustomWriteTarget(obj.getClass());
		if (target != null) {
			return conversionService.convert(obj, target);
		}

		if (BsonSimpleTypes.isSimpleType(obj.getClass())) {
			// Doesn't need conversion
			return getPotentiallyConvertedSimpleWrite(obj);
		}

		if (obj instanceof Map) {
			return maybeConvertMap((Map<?, ?>) obj);
		}

		if (obj.getClass().isArray()) {
			return maybeConvertArray(obj);
		}

		if (obj instanceof Iterable) {
			return maybeConvertList((Iterable<?>) obj);
		}

		return maybeConvertBean(obj);
	}

	private Object getPotentiallyConvertedSimpleWrite(Object value) {
		if (value == null) {
			return null;
		}

		Class<?> customTarget = conversions.getCustomWriteTarget(value.getClass(), null);

		if (customTarget != null) {
			return conversionService.convert(value, customTarget);
		} else {
			return Enum.class.isAssignableFrom(value.getClass()) ? ((Enum<?>) value).name() : value;
		}
	}

	private Object maybeConvertMap(Map<?, ?> map) {
		Document result = new Document();
		map.forEach((k, v) -> {
			result.put(k.toString(), toBSON(v));
		});
		return result;
	}

	private Object maybeConvertArray(Object array) {
		List<Object> result = new ArrayList<>();
		int length = Array.getLength(array);
		for (int i = 0; i < length; i++) {
			Object item = Array.get(array, i);
			result.add(toBSON(item));
		}
		return result;
	}

	private Object maybeConvertList(Iterable<?> source) {
		List<Object> result = new ArrayList<>();
		for (Object item : source) {
			result.add(toBSON(item));
		}
		return result;
	}

	private Object maybeConvertBean(Object obj) {
		Class<?> beanClass = obj.getClass();
		BeanMeta bm = BeanMeta.from(beanClass);
		if (!bm.isBeanClass()) {
			throw new BsonConvertException("指定对象类型不是 JavaBean" + beanClass.getSimpleName());
		}
		Document result = new Document();
		bm.properties.forEach((p, td) -> {
			Method readMethod = p.getReadMethod();
			try {
				if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
					readMethod.setAccessible(true);
				}
				Object value = readMethod.invoke(obj);
				if (td.hasAnnotation(ObjectId.class)) {
					value = conversionService.convert(value, org.bson.types.ObjectId.class);
				} else {
					value = toBSON(value);
				}

				String name = p.getName();
				if (td.hasAnnotation(_id.class)) {
					name = KEY_ID;
				}
				result.put(name, value);
			} catch (Throwable ex) {
				throw new BsonConvertException("无法转换" + p.getName() + "' from source to target", ex);
			}
		});
		return result;
	}

	@Override
	public <R> R fromBSON(Object source, Class<R> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

}
