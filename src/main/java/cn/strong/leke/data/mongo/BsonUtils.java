/**
 * 
 */
package cn.strong.leke.data.mongo;

import org.bson.Document;

import cn.strong.leke.data.mongo.convert.DefaultBsonConverter;

/**
 * BSON 转换工具类
 * 
 * @author liulongbiao
 *
 */
public class BsonUtils {

	private static final DefaultBsonConverter CONVERTER = new DefaultBsonConverter();

	/**
	 * 将 JavaBean 转换为 Mongo 文档
	 * 
	 * @param bean
	 * @return
	 */
	public static Document toBSON(Object bean) {
		return (Document) CONVERTER.toBSON(bean);
	}

	/**
	 * 将任意类型转换成 Document 所支持的 Bson 类型的值
	 * 
	 * @param obj
	 * @return
	 */
	public static Object toBsonValue(Object obj) {
		return CONVERTER.toBSON(obj);
	}

	/**
	 * 将 Mongo 文档转换为 JavaBean
	 * 
	 * @param doc
	 * @return
	 */
	public static <R> R fromBSON(Document doc, Class<R> clazz) {
		return CONVERTER.fromBSON(doc, clazz);
	}
}
