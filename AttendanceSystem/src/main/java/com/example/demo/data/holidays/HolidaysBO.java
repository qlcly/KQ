package com.example.demo.data.holidays;

public class HolidaysBO extends Holidays{
    public HolidaysBO(){
        super();
    }
    public static Holidays convert(Holidays holidays){
        Holidays holidaysInfo = new HolidaysBO();
        holidaysInfo.setEmpID(holidays.getEmpID());
        holidaysInfo.setYearHolidays(holidays.getYearHolidays());
        holidaysInfo.setPrivateHolidays(holidays.getPrivateHolidays());
        holidaysInfo.setSickHolidays(holidays.getSickHolidays());
        return holidaysInfo;
    }
}
