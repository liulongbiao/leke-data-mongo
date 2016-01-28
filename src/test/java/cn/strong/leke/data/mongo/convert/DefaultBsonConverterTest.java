package cn.strong.leke.data.mongo.convert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import cn.strong.leke.data.mongo.convert.DataStub.Assoc;

public class DefaultBsonConverterTest {

	private DataStub stub;
	private DefaultBsonConverter converter;

	@Before
	public void setup() {
		stub = new DataStub();
		stub.setId(new ObjectId().toString());
		stub.setName("stub");
		stub.setAge(23);
		stub.setImgdata("imgdata".getBytes());
		Assoc a1 = new Assoc();
		a1.setAssocId(new ObjectId().toString());
		a1.setName("assoc1");
		a1.setCreated(new Date());
		stub.setAssocs(Arrays.asList(a1));

		converter = new DefaultBsonConverter();
	}

	@Test
	public void testToBSON() {
		Object result = converter.toBSON(stub);
		assertTrue("result is a Document", result instanceof Document);
		Document doc = (Document) result;
		assertTrue("imgdata is a byte[]", doc.get("imgdata") instanceof byte[]);
		String json = doc.toJson(new JsonWriterSettings(true));
		System.out.println(json);
	}

	@Test
	public void testFromBSON() {
		Document doc = (Document) converter.toBSON(stub);

		DataStub result = converter.fromBSON(doc, DataStub.class);
		assertNotNull("result should not be null", result);
		assertTrue("imgdata should not loss", new String(result.getImgdata()).equals("imgdata"));
	}

}
