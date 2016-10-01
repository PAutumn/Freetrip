package LocalComparetor;

import java.util.Comparator;

import com.freetrip.trekker.bean.LocalItemBean.LocalInfo;

public class LocalSearchComparetor  implements Comparator<LocalInfo> {
	@Override
	public int compare(LocalInfo lhs, LocalInfo rhs) {
		if(lhs.similarity>rhs.similarity){
			return -1;
		}
		return 1;
	}

}
