package com.freetrip.trekker.bean;

import java.util.ArrayList;

/**
 * 景点详细信息的bean
 * @author mwp
 *
 */
public class DetailBean {

	public ArrayList<String> tops;
	public String wantto;
	public String visited;
	public String text;
	public double latitude;
	public double longitude;
	public String tel;
	public String url;
	@Override
	public String toString() {
		return "DetailBean [tops=" + tops + ", wantto=" + wantto + ", visited="
				+ visited + ", text=" + text + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", tel=" + tel + ", url=" + url
				+ "]";
	}
	
	
}
