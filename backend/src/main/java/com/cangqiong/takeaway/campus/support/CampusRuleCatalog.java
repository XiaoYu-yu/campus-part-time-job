package com.cangqiong.takeaway.campus.support;

import com.cangqiong.takeaway.campus.vo.CampusDeliveryRuleVO;

import java.math.BigDecimal;
import java.util.List;

public final class CampusRuleCatalog {

    private CampusRuleCatalog() {
    }

    public static final String PLATFORM_NAME = "重庆工信职业学院渝中校区校园代送平台";
    public static final int PRIORITY_WINDOW_MINUTES = 5;
    public static final int LOCATION_REPORT_INTERVAL_SECONDS = 60;
    public static final int LOCATION_REFRESH_INTERVAL_SECONDS = 60;
    public static final String CONTROLLED_FILE_PREFIX = "/api/files/";
    public static final BigDecimal BASE_FEE = new BigDecimal("3.00");
    public static final BigDecimal PRIORITY_FEE_MIN = new BigDecimal("3.00");
    public static final BigDecimal PRIORITY_FEE_MAX = new BigDecimal("5.00");
    public static final BigDecimal TIP_FEE_MIN = new BigDecimal("1.00");
    public static final BigDecimal TIP_FEE_MAX = new BigDecimal("10.00");

    public static final List<String> CAMPUS_ZONES = List.of("渝中校区");
    public static final List<String> DORMITORY_BUILDINGS = List.of("竹园", "杏园", "李园", "桃园", "梅园", "馨园");
    public static final List<String> TEACHING_BUILDINGS = List.of("一教学楼", "二教学楼", "世纪楼", "例行楼");
    public static final List<String> LIBRARY_POINTS = List.of("图书馆一楼门口", "图书馆二楼门口");

    public static CampusDeliveryRuleVO buildDeliveryRuleVO() {
        CampusDeliveryRuleVO vo = new CampusDeliveryRuleVO();
        vo.setPlatformName(PLATFORM_NAME);
        vo.setDormitoryBuildings(DORMITORY_BUILDINGS);
        vo.setTeachingBuildings(TEACHING_BUILDINGS);
        vo.setLibraryDeliveryPoints(LIBRARY_POINTS);
        vo.setPickupPointRules(List.of(
                "固定取餐点为主大门门卫室西侧临时取餐区和主大门外卖柜旁固定取餐区",
                "订单创建时必须选择一个有效取餐点"
        ));
        vo.setDormitoryRules(List.of(
                "宿舍单支付后先进入 5 分钟本楼栋优先窗口",
                "非本楼栋配送员在优先窗口结束后才可见单",
                "非本楼栋配送员不可进入宿舍楼层，只能送到楼下或门厅"
        ));
        vo.setTeachingBuildingRules(List.of(
                "教学楼默认送到楼下或门厅",
                "第一版不开放教室门口送达选项",
                "教师办公室订单仅支持送到指定办公室门口"
        ));
        vo.setLibraryRules(List.of(
                "图书馆只支持一楼门口和二楼门口",
                "第一版不细化馆内轨迹和具体座位"
        ));
        vo.setFeeRules(List.of(
                "基础代送费固定 3 元",
                "加急加价区间 3 到 5 元",
                "打赏区间 1 到 10 元",
                "第一版平台抽成固定为 0"
        ));
        vo.setPriorityWindowMinutes(PRIORITY_WINDOW_MINUTES);
        vo.setBaseFee(BASE_FEE);
        vo.setPriorityFeeMin(PRIORITY_FEE_MIN);
        vo.setPriorityFeeMax(PRIORITY_FEE_MAX);
        vo.setTipFeeMin(TIP_FEE_MIN);
        vo.setTipFeeMax(TIP_FEE_MAX);
        vo.setLocationReportIntervalSeconds(LOCATION_REPORT_INTERVAL_SECONDS);
        vo.setLocationRefreshIntervalSeconds(LOCATION_REFRESH_INTERVAL_SECONDS);
        vo.setLocationRefreshStrategy("配送员与用户端默认按 60 秒频率进行位置上报和刷新，不启用实时轨迹动画");
        return vo;
    }
}
