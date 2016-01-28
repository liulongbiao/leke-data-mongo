/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.util.Date;
import java.util.List;

import cn.strong.leke.data.mongo.annotations.ObjectId;
import cn.strong.leke.data.mongo.annotations._id;

/**
 * 数据类
 * 
 * @author liulongbiao
 *
 */
public class DataStub {
	@_id
	@ObjectId
	private String id;
	private String name;
	private Integer age;
	private byte[] imgdata;
	private List<Assoc> assoc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public byte[] getImgdata() {
		return imgdata;
	}

	public void setImgdata(byte[] imgdata) {
		this.imgdata = imgdata;
	}

	public List<Assoc> getNests() {
		return assoc;
	}

	public void setNests(List<Assoc> nests) {
		this.assoc = nests;
	}

	public static class Assoc {
		private String name;
		private Date created;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getCreated() {
			return created;
		}

		public void setCreated(Date created) {
			this.created = created;
		}

	}
}
