/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair;
import org.springframework.util.Assert;

/**
 * 可转换类型对注册
 * 
 * @author liulongbiao
 *
 */
public class ConverterRegistration {

	private final ConvertiblePair convertiblePair;
	private final boolean reading;
	private final boolean writing;

	public ConverterRegistration(ConvertiblePair convertiblePair, boolean isReading,
			boolean isWriting) {
		Assert.notNull(convertiblePair);
		this.convertiblePair = convertiblePair;
		this.reading = isReading;
		this.writing = isWriting;
	}

	public ConverterRegistration(Class<?> source, Class<?> target, boolean isReading,
			boolean isWriting) {
		this(new ConvertiblePair(source, target), isReading, isWriting);
	}

	/**
	 * 是否可用于写出
	 * 
	 * @return
	 */
	public boolean isWriting() {
		return writing == true || (!reading && isSimpleTargetType());
	}

	/**
	 * 是否可用于读取
	 * 
	 * @return
	 */
	public boolean isReading() {
		return reading == true || (!writing && isSimpleSourceType());
	}

	public ConvertiblePair getConvertiblePair() {
		return convertiblePair;
	}

	/**
	 * 源类型是否是简单类型
	 * 
	 * @return
	 */
	public boolean isSimpleSourceType() {
		return BsonSimpleTypes.isSimpleType(convertiblePair.getSourceType());
	}

	/**
	 * 目标类型是否是简单类型
	 * 
	 * @return
	 */
	public boolean isSimpleTargetType() {
		return BsonSimpleTypes.isSimpleType(convertiblePair.getTargetType());
	}
}
