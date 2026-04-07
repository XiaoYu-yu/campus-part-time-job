package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusSettlementRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Update("UPDATE campus_settlement_record SET " +
            "courier_profile_id = #{courierProfileId}, " +
            "gross_amount = #{grossAmount}, " +
            "platform_commission = #{platformCommission}, " +
            "pending_amount = #{pendingAmount}, " +
            "remark = #{remark}, " +
            "updated_at = #{updatedAt}, " +
            "settlement_status = CASE WHEN settlement_status = 'SETTLED' THEN settlement_status ELSE #{settlementStatus} END, " +
            "settled_at = CASE WHEN settlement_status = 'SETTLED' THEN settled_at ELSE NULL END " +
            "WHERE relay_order_id = #{relayOrderId}")
    int updateSnapshotForCompleted(
            @Param("relayOrderId") String relayOrderId,
            @Param("courierProfileId") Long courierProfileId,
            @Param("grossAmount") BigDecimal grossAmount,
            @Param("platformCommission") BigDecimal platformCommission,
            @Param("pendingAmount") BigDecimal pendingAmount,
            @Param("settlementStatus") String settlementStatus,
            @Param("remark") String remark,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}
