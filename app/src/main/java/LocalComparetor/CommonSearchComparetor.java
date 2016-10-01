package LocalComparetor;

import java.util.Comparator;

import com.freetrip.trekker.bean.CommonListBean.CommonInfo;

public class CommonSearchComparetor implements Comparator<CommonInfo> {
	@Override
	public int compare(CommonInfo lhs, CommonInfo rhs) {
		if(lhs.similarity>rhs.similarity){
			return -1;
		}
		return 1;
	}

}
