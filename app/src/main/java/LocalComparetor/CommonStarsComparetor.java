package LocalComparetor;

import java.util.Comparator;

import com.freetrip.trekker.bean.CommonListBean.CommonInfo;

public class CommonStarsComparetor implements Comparator<CommonInfo> {
	@Override
	public int compare(CommonInfo lhs, CommonInfo rhs) {
		if(lhs.star>rhs.star){
			return -1;
		}
		return 1;
	}

}
