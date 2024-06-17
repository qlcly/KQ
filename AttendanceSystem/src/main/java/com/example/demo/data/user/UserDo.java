package com.example.demo.data.user;


public class UserDo extends User
{
    private String password;
    public UserDo()
    {
        super();
    }


    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

}
