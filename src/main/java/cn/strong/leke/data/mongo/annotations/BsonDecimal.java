/**
 * 
 */
package cn.strong.leke.data.mongo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

/**
 * BSON 小数数值
 * 
 * @author liulongbiao
 *
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BsonDecimal {
	/**
	 * 小数位数
	 * 
	 * @return
	 */
	int scale() default 0;

	/**
	 * 小数截取模式
	 * 
	 * @return
	 */
	RoundingMode round() default RoundingMode.HALF_UP;
}
