/*
 * KnoxPatch
 * Copyright (C) 2022 BlackMesa123
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.unbound.patches.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unbound.patches.BuildConfig;
import com.unbound.patches.R;
import com.unbound.patches.databinding.ActivityInfoBinding;
import com.unbound.patches.ui.list.InfoListRoundedCorners;
import com.unbound.patches.ui.list.InfoListViewAdapter;

public class InfoActivity extends AppCompatActivity {
    private ActivityInfoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initAppBanner();
        setContentSideMargin(getResources().getConfiguration(),
                mBinding.mainContent);
        initListView();
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.mainToolbar);
        ActionBar toolBar = getSupportActionBar();
        toolBar.setDisplayHomeAsUpEnabled(true);
        toolBar.setDisplayShowTitleEnabled(false);
        mBinding.mainToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_app_info) {
            Intent intent = new Intent(
                    "android.settings.APPLICATION_DETAILS_SETTINGS",
                    Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return false;
    }

    private void initAppBanner() {
        mBinding.mainAppIcon.setImageDrawable(getPackageManager().getApplicationIcon(getApplicationInfo()));
        mBinding.mainAppVersion.setText(BuildConfig.VERSION_NAME);
        mBinding.mainAppGithub.setOnClickListener(new View.OnClickListener() {
            private long mLastClickTime;

            @Override
            public void onClick(View v) {
                long uptimeMillis = SystemClock.uptimeMillis();

                if (uptimeMillis - mLastClickTime > 600L) {
                    final String url = "https://github.com/LeeXDA21/UnboundPatches";

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(InfoActivity.this,
                                "No suitable activity found", Toast.LENGTH_SHORT).show();
                    }
                }

                mLastClickTime = uptimeMillis;
            }
        });
    }

    private void initListView() {
        RecyclerView listView = mBinding.mainList;
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(new InfoListViewAdapter(this));
        listView.addItemDecoration(new InfoListRoundedCorners(this));
        listView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        listView.seslSetFillBottomEnabled(true);
        listView.seslSetLastRoundedCorner(true);
    }

    private void setContentSideMargin(@NonNull Configuration config, @NonNull ViewGroup layout) {
        if (!isDestroyed() && !isFinishing()) {
            findViewById(android.R.id.content).post(() -> {
                if (config.screenWidthDp >= 589) {
                    layout.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                final int m = getSideMargin(config);

                ViewGroup.MarginLayoutParams lp
                        = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
                lp.setMargins(m, 0, m, 0);
                layout.setLayoutParams(lp);
            });
        }
    }

    private int getSideMargin(@NonNull Configuration config) {
        final int width = findViewById(android.R.id.content).getWidth();
        return (int) (width * getMarginRatio(config.screenWidthDp, config.screenHeightDp));
    }

    private float getMarginRatio(int screenWidthDp, int screenHeightDp) {
        if (screenWidthDp < 589) {
            return 0.0f;
        }
        if (screenHeightDp > 411 && screenWidthDp <= 959) {
            return 0.05f;
        }
        if (screenWidthDp >= 960 && screenHeightDp <= 1919) {
            return 0.125f;
        }
        if (screenWidthDp >= 1920) {
            return 0.25f;
        }

        return 0.0f;
    }
}