package com.knox.oauth.admin.commons.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.knox.oauth.admin.entities.ApplicationClient;
import com.knox.oauth.admin.entities.UserSso;


public class AplicacionesUsuario implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -4337407785767053521L;
    
    private UserSso user;
    private ApplicationClient app;
    private List<ApplicationClient> listApplicationsUsers = new ArrayList<>();
    private String listaId = new String();

    /**
     * @return the listaId
     */
    public String getListaId() {
        return listaId;
    }
    /**
     * @param listaId the listaId to set
     */
    public void setListaId(String listaId) {
        this.listaId = listaId;
    }
    /**
     * @return the listApplicationsUsers
     */
    public List<ApplicationClient> getListApplicationsUsers() {
        return listApplicationsUsers;
    }
    /**
     * @param listApplicationsUsers the listApplicationsUsers to set
     */
    public void setListApplicationsUsers(List<ApplicationClient> listApplicationsUsers) {
        this.listApplicationsUsers = listApplicationsUsers;
    }
    /**
     * @return the user
     */
    public UserSso getUser() {
        return user;
    }
    /**
     * @param user the user to set
     */
    public void setUser(UserSso user) {
        this.user = user;
    }
    /**
     * @return the app
     */
    public ApplicationClient getApp() {
        return app;
    }
    /**
     * @param app the app to set
     */
    public void setApp(ApplicationClient app) {
        this.app = app;
    }
}