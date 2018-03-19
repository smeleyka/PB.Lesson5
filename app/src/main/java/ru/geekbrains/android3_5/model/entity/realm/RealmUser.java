package ru.geekbrains.android3_5.model.entity.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by stanislav on 3/12/2018.
 */


public class RealmUser extends RealmObject
{
    @PrimaryKey
    private String login;
    private String avatarUrl;
    private RealmList<RealmUserRepository> repositories;

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }

    public RealmList<RealmUserRepository> getRepositories()
    {
        return repositories;
    }

    public void setRepositories(RealmList<RealmUserRepository> repositories)
    {
        this.repositories = repositories;
    }
}
