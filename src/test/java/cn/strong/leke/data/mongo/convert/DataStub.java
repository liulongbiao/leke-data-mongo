/**
 * 
 */
package cn.strong.leke.data.mongo.convert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.strong.leke.data.mongo.annotations.BsonDecimal;
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
	@BsonDecimal(scale = 2)
	private BigDecimal score;
	private List<Assoc> assocs;

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

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public List<Assoc> getAssocs() {
		return assocs;
	}

	public void setAssocs(List<Assoc> assocs) {
		this.assocs = assocs;
	}

	public static class Assoc {
		@ExtObjectId
		private String assocId;
		private String name;
		private Date created;

		public String getAssocId() {
			return assocId;
		}

		public void setAssocId(String assocId) {
			this.assocId = assocId;
		}

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
