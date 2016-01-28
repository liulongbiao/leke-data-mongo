package cn.strong.leke.data.mongo.convert;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import cn.strong.leke.data.mongo.annotations.ObjectId;
import cn.strong.leke.data.mongo.annotations._id;

public class BeanMetaTest {

	@Test
	public void test() {
		BeanMeta meta1 = BeanMeta.from(DataStub.class);
		meta1.properties.forEach((p, td) -> {
			System.out.println(String.format("%s : %s : %s", p.getName(),
					td.hasAnnotation(_id.class), td.hasAnnotation(ObjectId.class)));
			if ("id".equals(p.getName())) {
				assertTrue(td.hasAnnotation(_id.class));
				assertTrue(td.hasAnnotation(ObjectId.class));
			}
		});
		assertTrue(meta1.isBeanClass());

		BeanMeta meta2 = BeanMeta.from(List.class);
		assertFalse(meta2.isBeanClass());
	}

}
