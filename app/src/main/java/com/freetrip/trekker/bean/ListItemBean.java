package com.freetrip.trekker.bean;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
/**
 * 用于item的gson解析的Bean类
 * @author Administrator
 *
 */
public class ListItemBean {
	private String retcode;
	private Data data;
	
	public class Data{
		private ArrayList<Item> items;

		public ArrayList<Item> getItems() {
			return items;
		}

		public void setItems(ArrayList<Item> items) {
			this.items = items;
		}

		public Data(ArrayList<Item> items) {
			super();
			this.items = items;
		}
		
	}
	/**
	 * 详细信息类
	 * @author Administrator
	 *
	 */
	public class Item{
//		"img":"/title_0.jpg",//一张小的图标
//		   "text":"香港迪士尼乐园",//景点标题
//		   "location":"香港",//景点的地理位置
//		   "desc":"香港亲子游必去景点(一句话描述)",//一句话描述景点
//	           "url":"/item_0/item_0.json"//点击该景点之后去服务器拉取该景点对应的详细信息，跳入详细信息页面
		private String img;
		private String text;
		private String location;
		private String desc;
		private String url;
		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public Item(String img, String text, String location, String desc,
				String url) {
			super();
			this.img = img;
			this.text = text;
			this.location = location;
			this.desc = desc;
			this.url = url;
		}
		
		
	}

	public String getRetcode() {
		return retcode;
	}

	public void setRetcode(String retcode) {
		this.retcode = retcode;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public ListItemBean(String retcode, Data data) {
		super();
		this.retcode = retcode;
		this.data = data;
	}
	
	
}
