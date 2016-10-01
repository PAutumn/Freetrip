package com.freetrip.trekker.bean;

import java.util.List;

public class LocalItemBean {
	public int retcode;
	public Items data;
	@Override
	public String toString() {
		return "LocalItemBean [recode=" + retcode + ", data=" + data + "]";
	}
	
	public class Items{
		public List<LocalInfo> items;
		@Override
		public String toString() {
			return "Items [items=" + items + "]";
		}
	}
	public class LocalInfo{
   public String img;  //图片url
   public String text;  //标题
   public float instance;  //距离
   public String desc;  //描述
   public String url;   //详情url
   public String  feature; //特点 2-4个字
   public int praise;  //赞的数量
   public float star;  //星级
   public int similarity;
  
@Override
public String toString() {
	return "Info [img=" + img + ", text=" + text + ", desc=" + desc + ", url="
			+ url + ", feature=" + feature + ", praise=" + praise +"star"+star+ "]";
}
	}
}
