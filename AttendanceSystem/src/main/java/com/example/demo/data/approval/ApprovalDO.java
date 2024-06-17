package com.example.demo.data.approval;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ApprovalDO extends Approval implements RowMapper<Approval>
{
    public ApprovalDO()
    {
        super();
    }
    public static Approval convert(Approval approval){
        Approval approvalInfo = new ApprovalDO();
        approvalInfo.setAppID(approval.getAppID());
        approvalInfo.setEmpID(approval.getEmpID());
        approvalInfo.setApproval(approval.getApproval());
        approvalInfo.setComment(approval.getComment());
        approvalInfo.setType(approval.getType());
        approvalInfo.setTime(approval.getTime());
        return approvalInfo;
    }
    @Override
    public Approval mapRow(ResultSet rs, int i) throws SQLException {
        Approval approvalDo = new ApprovalDO();

        approvalDo.setEmpID(rs.getLong("empId"));
        approvalDo.setTime(rs.getDate("time"));
        approvalDo.setAppID(rs.getLong("appId"));
        approvalDo.setType(rs.getInt("approvalType"));
        approvalDo.setApproval(rs.getBoolean("approval"));
        approvalDo.setComment(rs.getString("comment"));

        return approvalDo;
    }
}
