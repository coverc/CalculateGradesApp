
package edu.uncc.inclass11;

import java.io.Serializable;

public class Grade implements Serializable {
    public String classCode, className, grade, creadted_by_uid, docID;
    public String creditHours, gpa;


    public Grade(String classCode, String className, String creditHours, String grade, String creadted_by_uid, String gpa, String docID){
        this.classCode = classCode;
        this.className = className;
        this.creditHours = creditHours;
        this.grade = grade;
        this.creadted_by_uid = creadted_by_uid;
        this.gpa = gpa;
        this.docID = docID;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getCreadted_by_uid() {
        return creadted_by_uid;
    }

    public void setCreadted_by_uid(String creadted_by_uid) {
        this.creadted_by_uid = creadted_by_uid;
    }

    public String getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(String creditHours) {
        this.creditHours = creditHours;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }
}
