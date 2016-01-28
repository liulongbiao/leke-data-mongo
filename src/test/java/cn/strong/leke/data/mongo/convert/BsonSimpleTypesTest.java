/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Pattern;

import org.bson.BsonBinary;
import org.bson.types.ObjectId;
import org.junit.Test;

/**
 * @author liulongbiao
 *
 */
public class BsonSimpleTypesTest {

	/**
	 * Test method for {@link cn.strong.leke.data.mongo.convert.BsonSimpleTypes#isSimpleType(java.lang.Class)}.
	 */
	@Test
	public void testIsSimpleType() {
		assertTrue(BsonSimpleTypes.isSimpleType(ObjectId.class));
		assertTrue(BsonSimpleTypes.isSimpleType(Pattern.class));
		assertTrue(BsonSimpleTypes.isSimpleType(BsonBinary.class));
		assertFalse(BsonSimpleTypes.isSimpleType(List.class));
	}

}
