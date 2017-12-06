package com.lecoder.team9.lecoder;

import java.io.Serializable;

/**
 * Created by Schwa on 2017-12-04.
 */

public class TimeTableItem implements Serializable {
    String classDay;
    String className;
    String classStartTime;
    String classEndTime;

    public String getClassDay() {
        return classDay;
    }

    public void setClassDay(String classDay) {
        this.classDay = classDay;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassStartTime() {
        return classStartTime;
    }

    public void setClassStartTime(String classStartTime) {
        this.classStartTime = classStartTime;
    }

    public String getClassEndTime() {
        return classEndTime;
    }

    public void setClassEndTime(String classEndTime) {
        this.classEndTime = classEndTime;
    }

    public TimeTableItem(String classDay, String className, String classStartTime, String classEndTime) {
        this.classDay = classDay;
        this.className = className;
        this.classStartTime = classStartTime;
        this.classEndTime = classEndTime;
    }
}
