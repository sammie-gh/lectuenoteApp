package com.example.hammer.lecturenoteapp.Model;

/**
 * Created by A.Richard on 19/09/2017.
 */

public class Pdf {

    private String Course,Description ,LecturesName,Date,YearGroup,Pdf;
    public String url;

    public Pdf() {
    }

    public Pdf(String course, String description, String lecturesName, String date, String yearGroup, String pdf, String url) {
        Course = course;
        Description = description;
        LecturesName = lecturesName;
        Date = date;//menuid
        YearGroup = yearGroup;
        Pdf = pdf;
        this.url = url;
    }

    public String getCourse() {
        return Course;
    }

    public void setCourse(String course) {
        Course = course;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLecturesName() {
        return LecturesName;
    }

    public void setLecturesName(String lecturesName) {
        LecturesName = lecturesName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getYearGroup() {
        return YearGroup;
    }

    public void setYearGroup(String yearGroup) {
        YearGroup = yearGroup;
    }

    public String getPdf() {
        return Pdf;
    }

    public void setPdf(String pdf) {
        Pdf = pdf;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
