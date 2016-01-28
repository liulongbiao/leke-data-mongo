/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import org.springframework.core.NestedRuntimeException;

/**
 * BSON 转换异常
 * 
 * @author yoyobill
 *
 */
public class BsonConvertException extends NestedRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9207880100536358842L;

	public BsonConvertException(String msg) {
		super(msg);
	}

	public BsonConvertException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
