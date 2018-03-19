package ru.geekbrains.android3_5.model.api;

/**
 * Created by stanislav on 3/12/2018.
 */

public class Repo
{
    protected ApiService getApi()
    {
        return ApiHolder.getInstance().getApi();
    }
}
