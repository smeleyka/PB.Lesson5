package ru.geekbrains.android3_5.model.api;

import java.util.List;

import javax.sql.DataSource;

import io.paperdb.Paper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.NetworkStatus;
import ru.geekbrains.android3_5.model.entity.User;
import ru.geekbrains.android3_5.model.entity.UserRepository;

/**
 * Created by stanislav on 3/12/2018.
 */

public class PaperUserRepo extends Repo implements IUserRepo
{
    public Observable<User> getUser(String username)
    {
        if(NetworkStatus.isOnline())
        {
            Observable<User> observable = getApi().getUser(username).subscribeOn(Schedulers.io());
            observable.subscribe(user -> Paper.book("data").write("user", user));
            return observable;
        }
        else
        {
            return Observable.create(e -> {

                if(Paper.book("data").contains("user"))
                {
                    e.onNext(Paper.book("data").read("user"));
                }
                else
                {
                    e.onError(new RuntimeException("No user in cache"));
                }
                e.onComplete();
            });
        }
    }

    public Observable<List<UserRepository>> getUserRepos(User user)
    {

        if(NetworkStatus.isOnline())
        {
            Observable<List<UserRepository>> observable = getApi().getUserRepos(user.getLogin()).subscribeOn(Schedulers.io());
            observable.subscribe(repos -> Paper.book("data").write("repos", repos));
            return observable;
        }
        else
        {
            return Observable.create(e -> {

                if(Paper.book("data").contains("repos"))
                {
                    e.onNext(Paper.book("data").read("repos"));
                }
                else
                {
                    e.onError(new RuntimeException("No user in cache"));
                }
                e.onComplete();
            });
        }
    }
}
