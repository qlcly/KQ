package com.example.demo.webservice.repository;

import com.example.demo.data.approval.Approval;
import com.example.demo.data.approval.ApprovalDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ApprovalRepository {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public ApprovalRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    //获取所有审批信息
    public List<Approval> getAllApproval(){
        String sql = "SELECT * FROM `approval` ORDER BY appId";
        List<Approval> approvalList = jdbcTemplate.query(sql, new ApprovalDO());
        return approvalList;
    }
    //获取某条审批信息
    //若empID为null，appID不为null，根据appID查询
    //若empID不为null，appID为null，根据empID查询
    //若都不为null，根据appID与empID查询
    public List<Approval> getApprovalByID(Long empID, Long appID){
        String sql = "SELECT * FROM `approval` WHERE empId=ifnull(?,empId) AND appId=ifnull(?,appId)";
        List<Approval> approval = jdbcTemplate.query(sql, new ApprovalDO(), empID,appID);
        return approval;
    }
    public Approval addApproval(Approval approval){
        String sql = "INSERT INTO `approval`(empId,time,appId,approvalType, approval,comment) VALUES(?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, approval.getEmpID(), approval.getTime(),approval.getAppID(),
                approval.getType(),approval.getApproval(),approval.getComment());
        String sql2 = "SELECT * FROM `approval` WHERE id=(select last_insert_id())";
        Approval approval2 = jdbcTemplate.queryForObject(sql2, new ApprovalDO());
        return approval2;
    }

    public Approval editApproval(long appID,Approval approval){
        String sql = "UPDATE `approval` SET empId = ?,time = ?,approvalType = ?,approval = ?,comment = ? WHERE appId=? and empId = ?";
        jdbcTemplate.update(sql,approval.getEmpID(), approval.getTime(),
                approval.getType(),approval.getApproval(),approval.getComment() ,appID, approval.getEmpID());
        return QueryApprovalByAppID(appID, approval.getEmpID());
    }
    public ResponseEntity deleteApproval(long appID, long empID){
        String sql = "DELETE FROM `approval` WHERE appId=? and empId = ?";
        jdbcTemplate.update(sql, appID, empID);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
    public void deleteApprovalByEmpID(long empID){
        String sql = "DELETE FROM `approval` WHERE appId=? and empID = ?";
        jdbcTemplate.update(sql, empID);
    }
    public Map<String, Object> queryByAppID(long appID, long empID){
        String sql = "SELECT * FROM `approval` WHERE empId=? and appId=? order by id desc limit 1";
        Map<String, Object> approvalMap = jdbcTemplate.queryForMap(sql, empID, appID);
        return approvalMap;
    }
    public Approval QueryApprovalByAppID(long appID, long empID){
        String sql = "select * from `approval` where appId = ? and empId = ? order by id desc limit 1";
        return jdbcTemplate.queryForObject(sql, new ApprovalDO(),appID, empID);
    }

    public Approval QueryLatestApprovalByAppID(long appID){
        String sql = "select * from `approval` where appId = ? order by id desc limit 1";
        return jdbcTemplate.queryForObject(sql, new ApprovalDO(),appID);
    }

}
