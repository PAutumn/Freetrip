package LocalComparetor;

import java.util.Comparator;

import com.freetrip.trekker.bean.LocalItemBean.LocalInfo;

public class LocalHotComparetor implements Comparator<LocalInfo> {
	@Override
	public int compare(LocalInfo lhs, LocalInfo rhs) {
		if(lhs.praise>rhs.praise){
			return -1;
		}
		return 1;
	}
}
