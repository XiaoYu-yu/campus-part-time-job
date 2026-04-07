package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusSettlementRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CampusSettlementRecordMapper {

    @Select("SELECT * FROM campus_settlement_record WHERE relay_order_id = #{relayOrderId}")
    CampusSettlementRecord selectByRelayOrderId(String relayOrderId);

    @Insert("INSERT INTO campus_settlement_record (" +
            "relay_order_id, courier_profile_id, gross_amount, platform_commission, pending_amount, settlement_status, " +
            "settled_at, remark, created_at, updated_at" +
            ") VALUES (" +
            "#{relayOrderId}, #{courierProfileId}, #{grossAmount}, #{platformCommission}, #{pendingAmount}, #{settlementStatus}, " +
            "#{settledAt}, #{remark}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusSettlementRecord record);
}
