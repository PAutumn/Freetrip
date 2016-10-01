package LocalComparetor;

import java.util.Comparator;

import com.freetrip.trekker.bean.CommonListBean.CommonInfo;

public class CommonHotComparetor implements Comparator<CommonInfo> {

	@Override
	public int compare(CommonInfo lhs, CommonInfo rhs) {
		if(lhs.praise>rhs.praise){
			return -1;
		}
		return 1;
	}

}
