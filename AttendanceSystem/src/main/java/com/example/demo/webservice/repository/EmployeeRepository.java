package com.example.demo.webservice.repository;

import com.example.demo.data.attendance.Attendance;
import com.example.demo.data.employee.Employee;
import com.example.demo.data.employee.EmployeeDO;
import com.example.demo.data.holidays.Holidays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EmployeeRepository {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    private HolidaysRepository holidaysRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    public EmployeeRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<Employee> getAllEmployeeInfo(){
        String sql = "select * from employee order by empId";
        List<Employee> employeesList = jdbcTemplate.query(sql, new EmployeeDO());
        return employeesList;
    }
    public Employee getEmployeeInfoByEmpID(long empID){
        String sql = "select * from employee where empId = ?";
        return jdbcTemplate.queryForObject(sql, new EmployeeDO(), empID);
    }
    public Employee addEmployeeInfo(Employee employee){
        String sql = "insert into employee(empId, empName, birthday, getJobDay, gender, deptId, deptName,auth) values (?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql, employee.getEmpID(),employee.getEmpName(),
                employee.getBirthday(), employee.getGetJobDay(), employee.getGender(),
                employee.getDeptID(),employee.getDeptName(),employee.getAuth());
        return getEmployeeInfoByEmpID(employee.getEmpID());
    }
    public Employee editEmployeeInfo(Employee employee){
        String sql = "update employee set empName=?,birthday=?,getJobDay=?,gender=?,deptId=?,deptName=?,auth=? where empId=?";
        jdbcTemplate.update(sql,employee.getEmpName(),employee.getBirthday(),employee.getGetJobDay(),
                employee.getGender(),employee.getDeptID(),employee.getDeptName(),
                employee.getAuth(),employee.getEmpID());
        return getEmployeeInfoByEmpID(employee.getEmpID());
    }
    public ResponseEntity deleteEmployeeInfo(long empID){
        String sql = "delete from employee where empId = ?";
        jdbcTemplate.update(sql, empID);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
    public Map<String, Object> QueryByEmpID(long empID){
        String sql = "select * from employee where empId = ?";
        Map<String,Object> employeeMap = jdbcTemplate.queryForMap(sql, empID);
        return employeeMap;
    }

    public Map<String, List<String>> getAttendanceReport()
    {
        Map<String, List<String>> report = new HashMap<>();
        List<Employee> employees = getAllEmployeeInfo();
        for (Employee employee: employees)
        {
            List<String> content = new ArrayList<>();
            Holidays holiday = holidaysRepository.getHolidaysInfoByID(employee.getEmpID());
            List<Attendance> attendances = attendanceRepository.getAttendanceByID(employee.getEmpID(), null);
            int absentCount = getWorkDays(new Date()) * 2 - attendances.size();
            content.add(String.valueOf(employee.getEmpID()));
            content.add(employee.getEmpName());
            content.add(employee.getDeptName());
            content.add(String.valueOf(holiday.getPrivateHolidays()));
            content.add(String.valueOf(absentCount));
            content.add(String.valueOf(holiday.getPrivateHolidays() + absentCount * 0.5));
            report.put(String.valueOf(employee.getEmpID()), content);
        }
        return report;
    }

    private int getWorkDays(Date date)
    {
        int workDays = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= daysOfMonth; i++)
        {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
            {
                workDays++;
            }
        }
        return workDays;
    }
}
