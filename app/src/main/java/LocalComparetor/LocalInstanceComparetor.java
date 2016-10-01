package LocalComparetor;

import java.util.Comparator;

import com.freetrip.trekker.bean.LocalItemBean.LocalInfo;

public class LocalInstanceComparetor implements Comparator<LocalInfo>{

	@Override
	public int compare(LocalInfo lhs, LocalInfo rhs) {
		if(lhs.instance>rhs.instance){
			return 1;
		}
		return -1;
	}
	
}