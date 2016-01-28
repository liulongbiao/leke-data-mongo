/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

/**
 * BSON 转换器接口
 * 
 * @author liulongbiao
 *
 */
public interface BsonConverter {
	/**
	 * 转换为 BSON 类型
	 * 
	 * @param obj
	 * @return
	 */
	Object toBSON(Object obj);

	/**
	 * 转换自 BSON 类型
	 * 
	 * @param obj
	 * @return
	 */
	<R> R fromBSON(Object source, Class<R> clazz);
}
