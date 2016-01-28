/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

/**
 * Bean 元数据
 * 
 * @author liulongbiao
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BeanMeta {

	public static final BeanMeta COLLECTION = new BeanMeta(Collection.class);
	public static final BeanMeta LIST = new BeanMeta(List.class);
	public static final BeanMeta SET = new BeanMeta(Set.class);
	public static final BeanMeta MAP = new BeanMeta(Map.class);
	public static final BeanMeta OBJECT = new BeanMeta(Object.class);

	private static final Map<Class, Reference<BeanMeta>> CACHE = Collections
			.synchronizedMap(new WeakHashMap<Class, Reference<BeanMeta>>());

	static {
		for (BeanMeta info : Arrays.asList(COLLECTION, LIST, SET, MAP, OBJECT)) {
			CACHE.put(info.type, new WeakReference<BeanMeta>(info));
		}
	}

	/**
	 * 从指定类型获取 BeanMeta 信息
	 * 
	 * @param type
	 * @return
	 */
	public static BeanMeta from(Class type) {
		Assert.notNull(type, "Type must not be null!");

		Reference<BeanMeta> cachedRef = CACHE.get(type);
		BeanMeta cached = cachedRef == null ? null : cachedRef.get();

		if (cached != null) {
			return (BeanMeta) cached;
		}

		BeanMeta result = new BeanMeta(type);
		CACHE.put(type, new WeakReference<BeanMeta>(result));
		return result;
	}

	public final Class type;
	public final Map<Property, TypeDescriptor> properties;

	BeanMeta(Class type) {
		this.type = type;
		this.properties = Collections.unmodifiableMap(findProperties(type));
	}

	/**
	 * 获取 JavaBean 的属性列表
	 * 
	 * @param type
	 * @return
	 */
	public static Map<Property, TypeDescriptor> findProperties(Class type) {
		if (BsonSimpleTypes.isSimpleType(type) || type.isArray()
				|| Iterable.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
			return Collections.emptyMap();
		}

		Map<Property, TypeDescriptor> map = new LinkedHashMap();
		for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(type)) {
			if ("class".equals(pd.getName())) {
				continue;
			}
			Property prop = new Property(type, pd.getReadMethod(), pd.getWriteMethod(),
					pd.getName());
			TypeDescriptor td = new TypeDescriptor(prop);
			map.put(prop, td);
		}
		return map;
	}

	/**
	 * 是否是 JavaBean 类型
	 * 
	 * @return
	 */
	public boolean isBeanClass() {
		return !properties.isEmpty();
	}
}
