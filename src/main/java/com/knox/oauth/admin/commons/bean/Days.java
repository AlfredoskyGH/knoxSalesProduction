package com.knox.oauth.admin.commons.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author team ssoinnova4j
 */
public class Days implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;
    private List<String> listDays;

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public List<String> getListDays(){
        listDays = new ArrayList<>();
        listDays.add(monday);
        listDays.add(tuesday);
        listDays.add(wednesday);
        listDays.add(thursday);
        listDays.add(friday);
        listDays.add(saturday);
        listDays.add(sunday);
        return listDays;
    }

    public Days setDays(String days){
        Days result = new Days();
        String[] listDays = days.split(" - ");

        for (String day: listDays) {
            if(day.equals("Lun")){
                result.setMonday(day);
            }
            if(day.equals("Mar")){
                result.setTuesday(day);
            }
            if(day.equals("Mie")){
                result.setWednesday(day);
            }
            if(day.equals("Jue")){
                result.setThursday(day);
            }
            if(day.equals("Vie")){
                result.setFriday(day);
            }
            if(day.equals("Sab")){
                result.setSaturday(day);
            }
            if(day.equals("Dom")){
                result.setSunday(day);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Days{" +
                "monday='" + monday + '\'' +
                ", tuesday='" + tuesday + '\'' +
                ", wednesday='" + wednesday + '\'' +
                ", thursday='" + thursday + '\'' +
                ", friday='" + friday + '\'' +
                ", saturday='" + saturday + '\'' +
                ", sunday='" + sunday + '\'' +
                '}';
    }
}
