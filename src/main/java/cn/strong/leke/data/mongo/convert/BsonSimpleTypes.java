/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.bson.BsonValue;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;
import org.springframework.util.Assert;

/**
 * Bson 简单类型
 * 
 * @author liulongbiao
 *
 */
public class BsonSimpleTypes {

	private static final Set<Class<?>> SIMPLE_TYPES;

	static {
		SIMPLE_TYPES = new CopyOnWriteArraySet<Class<?>>();
		SIMPLE_TYPES.add(boolean.class);
		SIMPLE_TYPES.add(long.class);
		SIMPLE_TYPES.add(short.class);
		SIMPLE_TYPES.add(int.class);
		SIMPLE_TYPES.add(byte.class);
		SIMPLE_TYPES.add(float.class);
		SIMPLE_TYPES.add(double.class);
		SIMPLE_TYPES.add(char.class);
		SIMPLE_TYPES.add(Boolean.class);
		SIMPLE_TYPES.add(Long.class);
		SIMPLE_TYPES.add(Short.class);
		SIMPLE_TYPES.add(Integer.class);
		SIMPLE_TYPES.add(Byte.class);
		SIMPLE_TYPES.add(Float.class);
		SIMPLE_TYPES.add(Double.class);
		SIMPLE_TYPES.add(Character.class);

		SIMPLE_TYPES.add(byte[].class);

		SIMPLE_TYPES.add(String.class);
		SIMPLE_TYPES.add(Date.class);
		SIMPLE_TYPES.add(Enum.class);

		SIMPLE_TYPES.add(Binary.class);
		SIMPLE_TYPES.add(MinKey.class);
		SIMPLE_TYPES.add(MaxKey.class);
		SIMPLE_TYPES.add(Code.class);
		SIMPLE_TYPES.add(ObjectId.class);
		SIMPLE_TYPES.add(Symbol.class);
		SIMPLE_TYPES.add(UUID.class);
		SIMPLE_TYPES.add(AtomicBoolean.class);
		SIMPLE_TYPES.add(AtomicInteger.class);
		SIMPLE_TYPES.add(AtomicLong.class);
		SIMPLE_TYPES.add(Pattern.class);
		
		SIMPLE_TYPES.add(BsonValue.class);
	}

	public static boolean isSimpleType(Class<?> type) {
		Assert.notNull(type);

		if (Object.class.equals(type)) { // Object 不作为基础类型
			return false;
		}

		if (SIMPLE_TYPES.contains(type)) {
			return true;
		}

		for (Class<?> clazz : SIMPLE_TYPES) {
			if (clazz.isAssignableFrom(type)) {
				SIMPLE_TYPES.add(type);
				return true;
			}
		}

		return false;
	}

	private BsonSimpleTypes() {
	}
}
