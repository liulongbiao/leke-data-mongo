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
import java.util.function.BiConsumer;

import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.core.CollectionFactory;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
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
			throw new BsonConvertException("指定对象类型不是 JavaBean " + beanClass.getSimpleName());
		}
		Document result = new Document();
		bm.properties.forEach(writeBeanProperty(obj, result));
		return result;
	}

	private BiConsumer<Property, TypeDescriptor> writeBeanProperty(Object obj, Document result) {
		return (p, td) -> {
			Method readMethod = p.getReadMethod();
			if (readMethod == null) {
				return;
			}
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
				throw new BsonConvertException(
						"无法转换 JavaBean " + obj.getClass().getSimpleName() + "中的" + p.getName() + "属性", ex);
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R fromBSON(Object source, Class<R> clazz) {
		return (R) read(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(clazz));
	}

	@SuppressWarnings("rawtypes")
	private Object read(Object dbo, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (null == dbo) {
			return null;
		}
		if (sourceType == null) {
			sourceType = TypeDescriptor.forObject(dbo);
		}
		if (targetType == null) {
			targetType = sourceType;
		}

		Class<?> targetClazz = targetType.getType();
		Class<?> sourceClazz = dbo.getClass();

		if (conversions.hasCustomReadTarget(sourceClazz, targetClazz)) {
			return conversionService.convert(dbo, targetClazz);
		}

		if (conversions.isSimpleType(dbo.getClass())) {
			// Doesn't need conversion
			return getPotentiallyConvertedSimpleRead(dbo, targetClazz);
		}

		if ((targetType.isCollection() || targetType.isArray()) && dbo instanceof Iterable) {
			return readCollectionOrArray((Iterable) dbo, sourceType, targetType);
		}

		if (targetType.isMap()) {
			return readMap(dbo, sourceType, targetType);
		}

		if (targetClazz.isAssignableFrom(sourceClazz)) {
			return dbo;
		}

		return readBean(dbo, sourceType, targetType);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getPotentiallyConvertedSimpleRead(Object value, Class<?> target) {
		if (value == null || target == null || target.isAssignableFrom(value.getClass())) {
			return value;
		}

		if (conversions.hasCustomReadTarget(value.getClass(), target)) {
			return conversionService.convert(value, target);
		}

		if (Enum.class.isAssignableFrom(target)) {
			return Enum.valueOf((Class<Enum>) target, value.toString());
		}

		return conversionService.convert(value, target);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object readCollectionOrArray(Iterable<?> dbo, TypeDescriptor sourceType, TypeDescriptor targetType) {
		ArrayList list = new ArrayList();
		if (dbo != null) {
			for (Object elem : dbo) {
				Object targetElem = read(elem, sourceType.elementTypeDescriptor(elem),
						targetType.getElementTypeDescriptor());
				list.add(targetElem);
			}
		}

		Class targetClazz = targetType.getType();
		if (targetClazz.isAssignableFrom(ArrayList.class)) {
			return list;
		}
		if (conversionService.canConvert(ArrayList.class, targetClazz)) {
			return conversionService.convert(list, targetClazz);
		}
		throw new BsonConvertException("无法读取集合或数组类型" + targetClazz.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	private Object readMap(Object dbo, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (!(dbo instanceof Map)) {
			throw new BsonConvertException("无法读取Map类型");
		}
		Map<String, Object> sourceMap = (Map<String, Object>) dbo;

		TypeDescriptor keyDesc = targetType.getMapKeyTypeDescriptor();
		TypeDescriptor valueDesc = targetType.getMapValueTypeDescriptor();

		Map<Object, Object> map = CollectionFactory.createMap(targetType.getType(),
				(keyDesc != null ? keyDesc.getType() : null), sourceMap.size());

		sourceMap.forEach((key, v) -> {
			Object targetKey = key;
			if (keyDesc != null && !keyDesc.getObjectType().isAssignableFrom(String.class)) {
				targetKey = conversionService.convert(key, keyDesc);
			}

			Object targetValue = read(v, sourceType.getMapValueTypeDescriptor(v), valueDesc);
			map.put(targetKey, targetValue);
		});

		return map;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object readBean(Object dbo, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (!(dbo instanceof Map)) {
			throw new BsonConvertException("无法读取非 Map 对象为 JavaBean");
		}
		Map<String, Object> sourceMap = (Map<String, Object>) dbo;
		Class beanClass = targetType.getType();
		BeanMeta bm = BeanMeta.from(beanClass);
		if (!bm.isBeanClass()) {
			throw new BsonConvertException("指定对象类型不是 JavaBean " + beanClass.getSimpleName());
		}
		Object result = BeanUtils.instantiate(beanClass);
		bm.properties.forEach(readBeanProperty(sourceMap, result));
		return result;
	}

	private BiConsumer<Property, TypeDescriptor> readBeanProperty(Map<String, Object> source, Object target) {
		return (p, td) -> {
			Method writeMethod = p.getWriteMethod();
			if (writeMethod == null) {
				return;
			}
			try {
				String name = p.getName();
				if (td.hasAnnotation(_id.class)) {
					name = KEY_ID;
				}
				Object value = source.get(name);
				Object targetValue = read(value, TypeDescriptor.forObject(value), td);

				if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
					writeMethod.setAccessible(true);
				}
				writeMethod.invoke(target, targetValue);
			} catch (Throwable ex) {
				throw new BsonConvertException(
						"无法读取 JavaBean " + p.getObjectType().getSimpleName() + "中的" + p.getName() + "属性",
						ex);
			}
		};
	}
}
