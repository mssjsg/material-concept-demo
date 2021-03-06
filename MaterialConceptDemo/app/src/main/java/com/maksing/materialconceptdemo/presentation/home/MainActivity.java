package com.maksing.materialconceptdemo.presentation.home;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.maksing.materialconceptdemo.R;
import com.maksing.materialconceptdemo.presentation.PresenterActivity;
import com.maksing.materialconceptdemo.presentation.navigation.NavigationMenuAdapter;
import com.maksing.materialconceptdemo.presentation.listing.MultiListsFragment;
import com.maksing.materialconceptdemo.presentation.PresenterFragment;
import com.maksing.materialconceptdemo.presentation.listing.SingleListFragment;
import com.maksing.materialconceptdemo.model.NavMenuItem;
import com.maksing.moviedbdomain.entity.Page;
import com.maksing.moviedbdomain.entity.User;
import com.maksing.moviedbdomain.usecase.configuration.GetNavItemsUseCase;
import com.maksing.moviedbdomain.usecase.session.GetUserDataUseCase;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by maksing on 22/12/14.
 */
public class MainActivity extends PresenterActivity<MainPresenter> implements MainView, View.OnClickListener {
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationMenuAdapter mNavigationMenuAdapter = new NavigationMenuAdapter(this);

    @InjectView(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.navMenu)
    RecyclerView mRecyclerView;
    @InjectView(R.id.username)
    TextView mUsername;

    private NavMenuItem mSelectedNavItem;

    private static final String KEY_SELETED_NAVITEM = "KEY_SELETED_NAVITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mNavigationMenuAdapter);

        if (savedInstanceState != null) {
            getPresenter().setCurrentNavMenuItem((NavMenuItem) savedInstanceState.getSerializable(KEY_SELETED_NAVITEM));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Now retrieve the DrawerLayout so that we can set the status bar color.
        // This only takes effect on Lollipop, or when using translucentStatusBar
        // on KitKat.


        TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.colorPrimaryDark});
        int colorResId = a.getResourceId(0, 0);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(colorResId));

        mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.shadow_nav_menu, Gravity.START);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_SELETED_NAVITEM, getPresenter().getCurrentNavMenuItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected MainPresenter onCreatePresenter() {
        logD("onCreatePresenter");
        return new MainPresenter(new GetNavItemsUseCase(getServiceHolder()), new GetUserDataUseCase(getServiceHolder()));
    }

    @Override
    public void gotoPage(Page page) {
        mDrawerLayout.closeDrawers();
        mNavigationMenuAdapter.notifyDataSetChanged();
        PresenterFragment fragment;
        if (page.getListsCount() > 1) {
            fragment = MultiListsFragment.createInstance(page);
        } else {
            fragment = SingleListFragment.createInstance(page);
        }
        switchPresenterFragment(R.id.page_container, fragment, page.getPath());
    }

    @Override
    public void updateNavigationMenu(List<NavMenuItem> navItems) {
        mNavigationMenuAdapter.setNavMenuItems(navItems);
    }

    @Override
    public void updateUser(User user) {
        mUsername.setText(user.getUsername());
    }

    @Override
    public void onClick(View v) {
        getPresenter().selectedNavItem((NavMenuItem)v.getTag());
    }
}
