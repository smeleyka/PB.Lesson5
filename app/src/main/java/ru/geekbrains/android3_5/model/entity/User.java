package ru.geekbrains.android3_5.model.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stanislav on 3/12/2018.
 */

public class User
{
    String avatarUrl;
    String login;
    List<UserRepository> repos = new ArrayList<>();

    public User(String login, String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
        this.login = login;
        this.repos = new ArrayList<>();
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public List<UserRepository> getRepos()
    {
        return repos;
    }

    public void setRepos(List<UserRepository> repos)
    {
        this.repos = repos;
    }
}
