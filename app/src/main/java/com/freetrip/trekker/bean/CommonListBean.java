package com.freetrip.trekker.bean;

import java.util.List;
public class CommonListBean {
	public int retcode;
	public Items data;
	@Override
	public String toString() {
		return "LocalItemBean [recode=" + retcode + ", data=" + data + "]";
	}
	
	public class Items{
		public List<CommonInfo> items;
		@Override
		public String toString() {
			return "Items [items=" + items + "]";
		}
	}
	public class CommonInfo{
   public String img;  //图片url
   public String text;  //标题
   public String desc;  //描述
   public String url;   //详情url
   public int praise;  //赞的数量
   public float star;  //星级\
   public String location;  //位置
   public int similarity;
  
@Override
public String toString() {
	return "Info [img=" + img + ", text=" + text + ", desc=" + desc + ", url="
			+ url + ", location=" + location + ", praise=" + praise +"star"+star+ "]";
}
	}
}
