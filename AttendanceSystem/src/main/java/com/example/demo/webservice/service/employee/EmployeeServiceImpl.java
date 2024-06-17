package com.example.demo.webservice.service.employee;

import com.example.demo.data.employee.Employee;
import com.example.demo.data.employee.EmployeeBO;
import com.example.demo.data.holidays.Holidays;
import com.example.demo.data.holidays.HolidaysVO;
import com.example.demo.webservice.repository.EmployeeRepository;
import com.example.demo.webservice.repository.HolidaysRepository;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


@Service
public class EmployeeServiceImpl implements EmployeeService{
    private EmployeeRepository employeeRepository;
    private HolidaysRepository holidaysRepository;
    private Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final static int SICK_HOLIDAYS = 5;
    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               HolidaysRepository holidaysRepository){
        this.employeeRepository = employeeRepository;
        this.holidaysRepository = holidaysRepository;
    }

    @Override
    public List<Employee> getAllEmployeeInfo() {
        return employeeRepository.getAllEmployeeInfo();
    }

    @Override
    public Employee getEmployeeInfoByEmpID(long empID) {
        return employeeRepository.getEmployeeInfoByEmpID(empID);
    }

    @Override
    public Employee addEmployeeInfo(Employee employee) {
        Employee employeeInfo = employeeRepository.addEmployeeInfo(employee);
        Holidays holidaysInfo = new HolidaysVO();
        holidaysInfo.setEmpID(employeeInfo.getEmpID());
        holidaysInfo.setYearHolidays(getCurrentYearAnnualLeaveDays(employeeInfo));
        holidaysInfo.setSickHolidays(SICK_HOLIDAYS);
        holidaysInfo.setPrivateHolidays(0);
        holidaysRepository.addHolidaysInfo(holidaysInfo);
        return employeeInfo;
    }

    @Override
    public Employee editEmployeeInfo(Employee employee) {
        long empID = employee.getEmpID();
        Map<String, Object> empMap = employeeRepository.QueryByEmpID(empID);
        Employee employeeBO = new EmployeeBO();
        BOFilled(employeeBO, empMap);
        BOUpdate(employeeBO, employee);
        return employeeRepository.editEmployeeInfo(employeeBO);
    }

    @Override
    public ResponseEntity deleteEmployeeInfo(long empID) {
        holidaysRepository.deleteHolidaysInfo(empID);
        return employeeRepository.deleteEmployeeInfo(empID);
    }

    @Override
    public ResponseEntity<byte[]> getAttendanceReport()
    {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        XSSFSheet sheet = workbook.createSheet("出勤报表（" + year + "年" + (month + 1) + "月）");
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        XSSFRow row = sheet.createRow(0);
        List<XSSFCell> headerCells = new ArrayList<>();
        for (int i = 0; i < 6; i++)
        {
            headerCells.add(row.createCell(i));
            headerCells.get(i).setCellStyle(headerStyle);
        }
        headerCells.get(0).setCellValue("员工编号");
        headerCells.get(1).setCellValue("员工姓名");
        headerCells.get(2).setCellValue("所属部门");
        headerCells.get(3).setCellValue("请事假数");
        headerCells.get(4).setCellValue("打卡缺勤数");
        headerCells.get(5).setCellValue("应扣工资天数");
        for (int i = 0; i < headerCells.size(); i++)
        {
            sheet.setColumnWidth(i, headerCells.get(i).getStringCellValue().getBytes(StandardCharsets.UTF_8).length * 256);
        }
        Map<String, List<String>> reportContent = employeeRepository.getAttendanceReport();
        XSSFCellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        int rowNum = 1;
        for (Map.Entry<String, List<String>> entry: reportContent.entrySet())
        {
            XSSFRow xssfRow = sheet.createRow(rowNum);
            List<String> rowContent = entry.getValue();
            for (int i = 0; i < rowContent.size(); i++)
            {
                XSSFCell xssfCell = xssfRow.createCell(i, CellType.STRING);
                xssfCell.setCellStyle(contentStyle);
                xssfCell.setCellValue(rowContent.get(i));
            }
            rowNum++;
        }
        try {
            workbook.write(outputStream);
        } catch (IOException e)
        {
            logger.warn("Unable to create attendance report", e);
        }
        byte[] content = outputStream.toByteArray();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDispositionFormData("file", "report.xlsx");
        httpHeaders.setContentLength(content.length);
        return new ResponseEntity<>(content, httpHeaders, HttpStatus.OK);
    }

    public void BOFilled(Employee employeeBO, Map<String, Object> employeeMap){
        for(Map.Entry<String, Object> entry: employeeMap.entrySet()){
            switch (entry.getKey()){
                case "empId":
                    employeeBO.setEmpID((Long)entry.getValue());
                    break;
                case "empName":
                    employeeBO.setEmpName((String) entry.getValue());
                    break;
                case "birthday":
                    employeeBO.setBirthday((Date) entry.getValue());
                    break;
                case "getJobDay":
                    employeeBO.setGetJobDay((Date) entry.getValue());
                    break;
                case "gender":
                    employeeBO.setGender((String) entry.getValue());
                    break;
                case "deptId":
                    employeeBO.setDeptID((Long)entry.getValue());
                    break;
                case "deptName":
                    employeeBO.setDeptName((String) entry.getValue());
                    break;
                case "auth":
                    employeeBO.setAuth((Integer) entry.getValue());
                    break;
            }
        }
    }
    public void BOUpdate(Employee employeeBO, Employee employeeVO){
        if(employeeVO.getEmpName() != null){
            employeeBO.setEmpName(employeeVO.getEmpName());
        }
        if(employeeVO.getBirthday() != null){
            employeeBO.setBirthday(employeeVO.getBirthday());
        }
        if(employeeVO.getDeptID() != null){
            employeeBO.setDeptID(employeeVO.getDeptID());
        }
        if(employeeVO.getDeptName() != null){
            employeeBO.setDeptName(employeeVO.getDeptName());
        }
        if(employeeVO.getAuth() != null){
            employeeBO.setAuth(employeeVO.getAuth());
        }
    }

    public int getWorkYears(java.util.Date getJobDay, java.util.Date present)
    {
        Calendar jobDay = Calendar.getInstance();
        jobDay.setTime(getJobDay);
        int getJobYear = jobDay.get(Calendar.YEAR);
        Calendar now = Calendar.getInstance();
        now.setTime(present);
        int currentYear = now.get(Calendar.YEAR);
        int count = 1;
        if (getJobYear == currentYear)
            return 0;
        jobDay.set(Calendar.YEAR, getJobYear + count);
        while (jobDay.before(now))
        {
            count++;
            jobDay.set(Calendar.YEAR, getJobYear + count);
        }
        return count - 1;
    }

    public int getCurrentYearAnnualLeaveDays(Employee employee)
    {
        Date getJobDay = employee.getGetJobDay();
        int workYears = getWorkYears(getJobDay, new java.util.Date());
        int availableLeaveDays = 0;
        if (workYears < 1)
            availableLeaveDays = 0;
        else if (workYears < 10)
            availableLeaveDays = 5;
        else if (workYears < 20)
            availableLeaveDays = 10;
        else availableLeaveDays = 15;
        return availableLeaveDays;
    }
}
