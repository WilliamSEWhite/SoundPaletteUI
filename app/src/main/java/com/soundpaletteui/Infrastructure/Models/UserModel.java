package com.soundpaletteui.Infrastructure.Models;

public class UserModel {

    int Id;
    private String Username;
    private String Password;

    public int getId(){
        return Id;
    }
    public String getUsername(){
        return Username;
    }
    public String getPassword(){
        return Password;
    }

    public UserModel(int id, String username, String password){
        this.Id = id;
        this.Username = username;
        this.Password = password;

    }

}
