package ru.geekbrains.android3_5.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.geekbrains.android3_5.R;
import ru.geekbrains.android3_5.model.image.ImageLoader;
import ru.geekbrains.android3_5.model.image.android.ImageLoaderGlide;
import ru.geekbrains.android3_5.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView
{
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ID = 0;

    private static final String[] permissons = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @BindView(R.id.iv_avatar) ImageView avatarImageView;
    @BindView(R.id.tv_error) TextView errorTextView;
    @BindView(R.id.tv_username) TextView usernameTextView;
    @BindView(R.id.pb_loading) ProgressBar loadingProgressBar;
    @BindView(R.id.rv_repos) RecyclerView reposRecyclerView;

    RepoRVAdapter adapter;

    MainPresenter presenter;

    ImageLoader<ImageView> imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imageLoader = new ImageLoaderGlide();
        presenter = new MainPresenter(this, AndroidSchedulers.mainThread());

        reposRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reposRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new RepoRVAdapter(presenter);
        reposRecyclerView.setAdapter(adapter);

        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    onPermissionsGranted();
                }
                else
                {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.permissons_required)
                            .setMessage(R.string.permissions_required_message)
                            .setPositiveButton("OK", (dialog, which) -> requestPermissions())
                            .setOnCancelListener(dialog -> requestPermissions())
                            .create()
                            .show();
                }
            }
        }
    }

    @Override
    public void showAvatar(String avatarUrl)
    {
        imageLoader.loadInto(avatarUrl, avatarImageView);
    }

    @Override
    public void showError(String message)
    {

        errorTextView.setText(message);
    }

    @Override
    public void showLoading()
    {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading()
    {
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void updateRepoList()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setUsername(String username)
    {
        usernameTextView.setText(username);
    }


    private void checkPermissions()
    {
        for(String permission : permissons)
        {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions();
                return;
            }
        }

       onPermissionsGranted();
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this, permissons, PERMISSIONS_REQUEST_ID);
    }

    private void onPermissionsGranted()
    {
        presenter.onPermissionsGranted();
    }
}
