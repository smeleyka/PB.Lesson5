package ru.geekbrains.android3_5.model.api;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import ru.geekbrains.android3_5.NetworkStatus;
import ru.geekbrains.android3_5.model.entity.User;
import ru.geekbrains.android3_5.model.entity.UserRepository;
import ru.geekbrains.android3_5.model.entity.realm.RealmUser;
import ru.geekbrains.android3_5.model.entity.realm.RealmUserRepository;

/**
 * Created by stanislav on 3/12/2018.
 */

public class RealmUserRepo extends Repo implements IUserRepo
{
    private static final String TAG = "ActiveAndroidUserRepo";

    public Observable<User> getUser(String username)
    {
        if (NetworkStatus.isOnline())
        {
            Observable<User> observable = getApi().getUser(username).subscribeOn(Schedulers.io());
            observable.subscribe(user -> {

                Realm realm = Realm.getDefaultInstance();
                RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();

                if(realmUser == null)
                {
                    realm.executeTransaction(innerRealm ->
                    {
                        RealmUser newRealmUser = realm.createObject(RealmUser.class, user.getLogin());
                        newRealmUser.setAvatarUrl(user.getAvatarUrl());
                    });
                }
                else
                {
                    realm.executeTransaction(innerRealm ->
                    {
                        realmUser.setAvatarUrl(user.getAvatarUrl());
                    });
                }

                realm.close();

            });

            return observable;
        }
        else
        {
            return Observable.create(e ->
            {
                Realm realm = Realm.getDefaultInstance();
                RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", username).findFirst();

                if (realmUser == null)
                {
                    e.onError(new RuntimeException("No user in cache"));
                }
                else
                {
                    e.onNext(new User(realmUser.getLogin(), realmUser.getAvatarUrl()));
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
                Realm realm = Realm.getDefaultInstance();
                RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

                if(realmUser == null)
                {
                    realm.executeTransaction(innerRealm ->
                    {
                        RealmUser newRealmUser = realm.createObject(RealmUser.class, user.getLogin());
                        newRealmUser.setAvatarUrl(user.getAvatarUrl());
                    });
                }

                realm.executeTransaction(innerRealm -> {

                    realmUser.getRepositories().deleteAllFromRealm();

                    for(UserRepository repository : repos)
                    {
                        RealmUserRepository realmUserRepository = realm.createObject(RealmUserRepository.class, repository.getId());
                        realmUserRepository.setName(repository.getName());
                        realmUser.getRepositories().add(realmUserRepository);
                    }

                });

            });
            return observable;
        }
        else
        {
            return Observable.create(e ->
            {
                Realm realm = Realm.getDefaultInstance();
                RealmUser realmUser = realm.where(RealmUser.class).equalTo("login", user.getLogin()).findFirst();

                if (realmUser == null)
                {
                    e.onError(new RuntimeException("No user in cache"));
                }
                else
                {
                    List<UserRepository> repos = new ArrayList<>();
                    for (RealmUserRepository realmUserRepository : realmUser.getRepositories())
                    {
                        repos.add(new UserRepository(realmUserRepository.getId(), realmUserRepository.getName()));
                    }
                    e.onNext(repos);
                }
                e.onComplete();
            });
        }
    }
}
