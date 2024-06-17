package com.example.demo.data.approval;



public class ApprovalVO extends Approval
{
    public ApprovalVO()
    {
        super();
    }

    public static Object[] ApprovalList(Approval approval){
        Object[] fieldList = {approval.getApproval(), approval.getEmpID(), approval.getType(),
                approval.getComment(), approval.getAppID()};
        return fieldList;
    }
}
