package ru.geekbrains.android3_5.presenter;

import android.util.Log;

import io.reactivex.Scheduler;
import ru.geekbrains.android3_5.model.api.ActiveAndroidUserRepo;
import ru.geekbrains.android3_5.model.api.IUserRepo;
import ru.geekbrains.android3_5.model.api.PaperUserRepo;
import ru.geekbrains.android3_5.model.api.RealmUserRepo;
import ru.geekbrains.android3_5.model.entity.User;
import ru.geekbrains.android3_5.view.MainView;
import ru.geekbrains.android3_5.view.RepoRowView;

/**
 * Created by stanislav on 3/12/2018.
 */

public class MainPresenter implements IRepoListPresenter
{
    private static final String TAG = "MainPresenter";
    private MainView view;
    private Scheduler scheduler;
    private IUserRepo userRepo;

    private User user;

    public MainPresenter(MainView view, Scheduler scheduler)
    {
        this.view = view;
        this.scheduler = scheduler;
        userRepo = new RealmUserRepo();
    }

    public void loadInfo()
    {
        userRepo.getUser("smeleyka").subscribe(user -> {
            this.user = user;
            userRepo.getUserRepos(user)
                    .observeOn(scheduler)
                    .subscribe(userRepositories -> {
                       this.user.setRepos(userRepositories);
                        view.hideLoading();
                        view.showAvatar(user.getAvatarUrl());
                        view.setUsername(user.getLogin());
                        view.updateRepoList();
                    }, throwable -> {
                        Log.e(TAG, "Failed to get user repos", throwable);
                        view.showError(throwable.getMessage());
                        view.hideLoading();
                    });
        }, throwable -> {
            Log.e(TAG, "Failed to get user", throwable);
            view.showError(throwable.getMessage());
            view.hideLoading();
        });

    }

    public void onPermissionsGranted()
    {
        loadInfo();
    }

    @Override
    public void bindRepoListRow(int pos, RepoRowView rowView)
    {
        if(user != null)
        {
            rowView.setTitle(user.getRepos().get(pos).getName());
        }
    }

    @Override
    public int getRepoCount()
    {
        return user == null ? 0 : user.getRepos().size();
    }
}
