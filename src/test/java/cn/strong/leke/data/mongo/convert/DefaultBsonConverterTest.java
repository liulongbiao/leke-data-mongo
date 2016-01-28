package cn.strong.leke.data.mongo.convert;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.junit.Test;

import cn.strong.leke.data.mongo.convert.DataStub.Assoc;

public class DefaultBsonConverterTest {

	@Test
	public void testToBSON() {
		DataStub stub = new DataStub();
		stub.setId(new ObjectId().toString());
		stub.setName("stub");
		stub.setAge(23);
		stub.setImgdata("imgdata".getBytes());
		Assoc a1 = new Assoc();
		a1.setName("assoc1");
		a1.setCreated(new Date());
		stub.setAssocs(Arrays.asList(a1));

		DefaultBsonConverter converter = new DefaultBsonConverter();
		Object result = converter.toBSON(stub);
		assertTrue("result is a Document", result instanceof Document);
		Document doc = (Document) result;
		assertTrue("imgdata is a byte[]", doc.get("imgdata") instanceof byte[]);
		String json = doc.toJson(new JsonWriterSettings(true));
		System.out.println(json);
	}

	// @Test
	public void testFromBSON() {
		fail("Not yet implemented");
	}

}
