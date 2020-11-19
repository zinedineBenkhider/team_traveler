package com.example.teamtraveler.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * class for the date Manipulaion
 */
public class ManipulateDate {

    /**
     * convert a string date on french format date
     * @param date the string to convert
     * @return the string date at a french format
     */
    public static String getDateFormatFrench(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(Date.parse(date));
    }

    /**
     * convert a string date into a int array
     * @param dateStr the string date
     * @return the int array
     */
    public static int[] parseDate(String dateStr){
        int[] res = new int[3];
        String[] date=dateStr.split("/");
        if(date.length==3){
            res[0]=Integer.parseInt(date[0]);
            res[1]=Integer.parseInt(date[1])-1;
            res[2]=Integer.parseInt(date[2]);
        }
        else{
            Calendar cldr = Calendar.getInstance();
            res[0]= cldr.get(Calendar.DAY_OF_MONTH);
            res[1]=cldr.get(Calendar.MONTH);
            res[2]=cldr.get(Calendar.YEAR);
        }
        return res;
    }

    /**
     * return 0 if d1 == d2
     * return < 0 if d1 < d2 (d1 before d2)
     * return > 0 if d1 > d2 (d1 after d2)
     * @param d1 the first date
     * @param d2 the second date
     * @return the comparison between the date
     */
    public static int compareDate(Date d1, Date d2) {
        Calendar calendar1 = new GregorianCalendar();
        calendar1.setTime(d1);
        Calendar calendar2 = new GregorianCalendar();
        calendar2.setTime(d2);
        if (calendar1.get(Calendar.YEAR) != calendar2.get(Calendar.YEAR))
            return calendar1.get(Calendar.YEAR) - calendar2.get(Calendar.YEAR);
        if (calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH))
            return calendar1.get(Calendar.MONTH) - calendar2.get(Calendar.MONTH);
        return calendar1.get(Calendar.DATE) - calendar2.get(Calendar.DATE);
    }

    /**
     * convert a string to a date or display an error
     * @param dateStr the string to convert
     * @return a Date
     */
    public static Date strTodDate(String dateStr)  {

        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        } catch (ParseException e) {

            Logger.getLogger(e.toString());
        }
        return null;
    }

    /**
     * return the time of the date (in milliseconds)
     * @param dateStr the string date
     * @return the time in miliseconds
     */
    public static long getTimeOfDate(String dateStr){
        int date[]=ManipulateDate.parseDate(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.set(date[2],date[1],date[0]);
        long timeDate=calendar.getTimeInMillis();
        return timeDate;
    }

    /**
     * return a time in right format
     * @param hours the hours to display
     * @param minutes the minutes to display
     * @return a string corresponding to the time given
     */
    public static String getFormatTime(int hours,int minutes){
        if(hours>=10 && minutes>=10){
            return hours+":"+minutes;
        }
        else if(hours>=10 && minutes<10){
            return hours+":0"+minutes;
        }
        else if(hours<10 && minutes>=10){
            return "0"+hours+":"+minutes;
        }

        else {
            return "0"+hours+":0"+minutes;
        }
    }

}
