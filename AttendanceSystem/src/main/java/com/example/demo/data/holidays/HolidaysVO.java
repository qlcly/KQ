package com.example.demo.data.holidays;

public class HolidaysVO extends Holidays{
    public HolidaysVO(){
        super();
    }
    public static Holidays convert(Holidays holidays){
        Holidays holidaysInfo = new HolidaysVO();
        holidaysInfo.setEmpID(holidays.getEmpID());
        holidaysInfo.setYearHolidays(holidays.getYearHolidays());
        holidaysInfo.setPrivateHolidays(holidays.getPrivateHolidays());
        holidaysInfo.setSickHolidays(holidays.getSickHolidays());
        return holidaysInfo;
    }
    public static Object[] HolidaysList(Holidays holidays){
        Object[] holidaysList = {holidays.getYearHolidays(), holidays.getPrivateHolidays(),
                holidays.getSickHolidays(), holidays.getEmpID()};
        return holidaysList;
    }
}
