package LocalComparetor;

import java.util.Comparator;

import com.freetrip.trekker.bean.LocalItemBean.LocalInfo;

public class LocalStarsComparetor implements Comparator<LocalInfo> {
	@Override
	public int compare(LocalInfo lhs, LocalInfo rhs) {
		if(lhs.star>rhs.star){
			return -1;
		}
		return 1;
	}

}
