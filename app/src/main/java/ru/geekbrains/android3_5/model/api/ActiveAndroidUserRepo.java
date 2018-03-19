package ru.geekbrains.android3_5.model.api;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import ru.geekbrains.android3_5.NetworkStatus;
import ru.geekbrains.android3_5.model.entity.User;
import ru.geekbrains.android3_5.model.entity.UserRepository;
import ru.geekbrains.android3_5.model.entity.activeandroid.AAUser;
import ru.geekbrains.android3_5.model.entity.activeandroid.AAUserRepository;

/**
 * Created by stanislav on 3/12/2018.
 */

public class ActiveAndroidUserRepo extends Repo implements IUserRepo
{
    private static final String TAG = "ActiveAndroidUserRepo";

    public Observable<User> getUser(String username)
    {
        if (NetworkStatus.isOnline())
        {
            Observable<User> observable = getApi().getUser(username).subscribeOn(Schedulers.io());
            observable.subscribe(user -> {
                AAUser aaUser = new Select()
                        .from(AAUser.class)
                        .where("login = ?", username)
                        .executeSingle();

                if (aaUser == null)
                {
                    aaUser = new AAUser();
                    aaUser.login = user.getLogin();
                }

                aaUser.avatarUrl = user.getAvatarUrl();
                aaUser.save();
            });

            return observable;
        } else
        {
            return Observable.create(e ->
            {
                AAUser aaUser = new Select()
                        .from(AAUser.class)
                        .where("login = ?", username)
                        .executeSingle();


                if (aaUser == null)
                {
                    e.onError(new RuntimeException("No user in cache"));
                }
                else
                {
                    e.onNext(new User(aaUser.login, aaUser.avatarUrl));
                }
                e.onComplete();
            });
        }
    }

    public Observable<List<UserRepository>> getUserRepos(User user)
    {
        if (NetworkStatus.isOnline())
        {
            Observable<List<UserRepository>> observable = getApi().getUserRepos(user.getLogin()).subscribeOn(Schedulers.io());
            observable.subscribe(repos ->
            {
                AAUser aaUser = null;
                try
                {
                    aaUser = new Select()
                            .from(AAUser.class)
                            .where("login = ?", user.getLogin())
                            .executeSingle();
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "Failed to get user", ex);
                }

                if (aaUser == null)
                {
                    aaUser = new AAUser();
                    aaUser.login = user.getLogin();
                    aaUser.avatarUrl = user.getAvatarUrl();
                    aaUser.save();
                }

                new Delete().from(AAUserRepository.class).where("user = ?", aaUser);

                ActiveAndroid.beginTransaction();
                try
                {
                    for (UserRepository repo : repos)
                    {
                        AAUserRepository aaUserRepository = new AAUserRepository();
                        aaUserRepository.id = repo.getId();
                        aaUserRepository.name = repo.getName();
                        aaUserRepository.user = aaUser;
                        aaUserRepository.save();
                    }

                    ActiveAndroid.setTransactionSuccessful();
                }
                finally
                {
                    ActiveAndroid.endTransaction();
                }
            });
            return observable;
        } else
        {
            return Observable.create(e ->
            {

                AAUser aaUser = new Select()
                        .from(AAUser.class)
                        .where("login = ?", user.getLogin())
                        .executeSingle();

                if (aaUser == null)
                {
                    e.onError(new RuntimeException("No user in cache"));
                } else
                {
                    List<UserRepository> repos = new ArrayList<>();
                    for (AAUserRepository aaUserRepository : aaUser.repositories())
                    {
                        repos.add(new UserRepository(aaUserRepository.id, aaUserRepository.name));
                    }
                    e.onNext(repos);
                }
                e.onComplete();
            });
        }
    }
}
