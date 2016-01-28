/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.strong.leke.data.mongo.annotations.ObjectId;

/**
 * @author yoyobill
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@ObjectId
public @interface ExtObjectId {

}
