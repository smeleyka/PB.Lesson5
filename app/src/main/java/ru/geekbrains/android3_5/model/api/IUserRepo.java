package ru.geekbrains.android3_5.model.api;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.model.entity.User;
import ru.geekbrains.android3_5.model.entity.UserRepository;

/**
 * Created by stanislav on 3/12/2018.
 */

public interface IUserRepo
{
    Observable<User> getUser(String username);
    Observable<List<UserRepository>> getUserRepos(User user);

}
