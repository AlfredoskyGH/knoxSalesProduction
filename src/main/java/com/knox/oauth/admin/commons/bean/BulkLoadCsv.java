package com.knox.oauth.admin.commons.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.knox.oauth.admin.entities.UserSso;

/**
 *@author team ssoinnova4j
 *
 *Clase que representa la estructura del archivo csv.
 *
 */

public class BulkLoadCsv implements Serializable{
	//TODO evaluar guardar en bitacora en bd de carga masiva.
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String nameFile;
    private Date dateFile;
    private boolean validateFormat;
    private String errorFormat;
    private List<UserSso> usersLoad;
    private List<String> errorField;
    private int total = 0;
    private int processed = 0;
    private int noProcessed = 0;

    public BulkLoadCsv() {
        this.usersLoad = new ArrayList<>();
        this.errorField = new ArrayList<>();
    }

    public BulkLoadCsv(String nameFile, Date dateFile, boolean validateFormat, String errorFormat, List<UserSso> usersLoad, List<String> errorField, int total, int processed, int noProcessed) {
        this.nameFile = nameFile;
        this.dateFile = dateFile;
        this.validateFormat = validateFormat;
        this.errorFormat = errorFormat;
        this.usersLoad = usersLoad;
        this.errorField = errorField;
        this.total = total;
        this.processed = processed;
        this.noProcessed = noProcessed;
        this.usersLoad = new ArrayList<>();
        this.errorField = new ArrayList<>();
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public Date getDateFile() {
        return dateFile;
    }

    public void setDateFile(Date dateFile) {
        this.dateFile = dateFile;
    }

    public boolean isValidateFormat() {
        return validateFormat;
    }

    public void setValidateFormat(boolean validateFormat) {
        this.validateFormat = validateFormat;
    }

    public String getErrorFormat() {
        return errorFormat;
    }

    public void setErrorFormat(String errorFormat) {
        this.errorFormat = errorFormat;
    }

    public List<UserSso> getUsersLoad() {
        return usersLoad;
    }

    public void setUsersLoad(List<UserSso> usersLoad) {
        this.usersLoad = usersLoad;
    }

    public void addUsersLoad(UserSso usersLoad) {
        this.usersLoad.add(usersLoad);
    }

    public List<String> getErrorField() {
        return errorField;
    }

    public void setErrorField(List<String> errorField) {
        this.errorField = errorField;
    }

    public void setErrorField(ArrayList<String> errorField) {
        this.errorField = errorField;
    }

    public void addErrorField(String errorField) {
        this.errorField.add(errorField);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void totalAcumulator() {
        this.total++;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public void processed() {
        this.processed++;
    }

    public int getNoProcessed() {
        return noProcessed;
    }

    public void setNoProcessed(int noProcessed) {
        this.noProcessed = noProcessed;
    }

    public void noProcessed() {
        this.noProcessed++;
    }

    @Override
    public String toString() {
        return "BulkLoadCsv[" +
                "nameFile='" + nameFile + '\'' +
                ", dateFile=" + dateFile +
                ", validateFormat=" + validateFormat +
                ", errorFormat='" + errorFormat + '\'' +
                ", usersLoad=" + usersLoad +
                ", errorField=" + errorField +
                ", total=" + total +
                ", processed=" + processed +
                ", noProcessed=" + noProcessed +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BulkLoadCsv that = (BulkLoadCsv) o;

        if (validateFormat != that.validateFormat) return false;
        if (total != that.total) return false;
        if (processed != that.processed) return false;
        if (noProcessed != that.noProcessed) return false;
        if (nameFile != null ? !nameFile.equals(that.nameFile) : that.nameFile != null) return false;
        if (dateFile != null ? !dateFile.equals(that.dateFile) : that.dateFile != null) return false;
        if (errorFormat != null ? !errorFormat.equals(that.errorFormat) : that.errorFormat != null) return false;
        if (usersLoad != null ? !usersLoad.equals(that.usersLoad) : that.usersLoad != null) return false;
        return errorField != null ? errorField.equals(that.errorField) : that.errorField == null;
    }

    @Override
    public int hashCode() {
        int result = nameFile != null ? nameFile.hashCode() : 0;
        result = 31 * result + (dateFile != null ? dateFile.hashCode() : 0);
        result = 31 * result + (validateFormat ? 1 : 0);
        result = 31 * result + (errorFormat != null ? errorFormat.hashCode() : 0);
        result = 31 * result + (usersLoad != null ? usersLoad.hashCode() : 0);
        result = 31 * result + (errorField != null ? errorField.hashCode() : 0);
        result = 31 * result + total;
        result = 31 * result + processed;
        result = 31 * result + noProcessed;
        return result;
    }
}
