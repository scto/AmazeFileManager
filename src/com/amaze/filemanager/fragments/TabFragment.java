package com.amaze.filemanager.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amaze.filemanager.R;
import com.amaze.filemanager.activities.MainActivity;
import com.amaze.filemanager.adapters.TabSpinnerAdapter;
import com.amaze.filemanager.database.Tab;
import com.amaze.filemanager.database.TabHandler;
import com.amaze.filemanager.utils.Futils;
import com.amaze.filemanager.utils.Shortcuts;
import com.amaze.filemanager.utils.ZipObj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arpit on 15-12-2014.
 */
public class TabFragment extends android.support.v4.app.Fragment {

    public  List<Fragment> fragments = new ArrayList<Fragment>();
    public ScreenSlidePagerAdapter mSectionsPagerAdapter;
    android.support.v4.view.PagerTitleStrip STRIP;
    Futils utils = new Futils();
    public ViewPager mViewPager;
    SharedPreferences Sp;
    TabFragment t = this;
    String path = "",path1="",path0="",zippath="";
    int currenttab;
    MainActivity mainActivity;
    TabSpinnerAdapter tabSpinnerAdapter;
    public ArrayList<String> tabs=new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tabfragment,
                container, false);
        Sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        path0= Sp.getString("tab0","");
        path1= Sp.getString("tab1","/");
       // Toast.makeText(getActivity(),path0,Toast.LENGTH_LONG).show();
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        STRIP = ((android.support.v4.view.PagerTitleStrip) rootView
                .findViewById(R.id.pager_title_strip));
        STRIP.setBackgroundDrawable(new ColorDrawable(Color.parseColor(((MainActivity)getActivity()).skin)));
        if (getArguments() != null){
            path = getArguments().getString("path");
            zippath=getArguments().getString("zippath");
        }
        mainActivity = ((MainActivity)getActivity());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrolled(int p1, float p2, int p3) {
                String name=fragments.get(mViewPager.getCurrentItem()).getClass().getName();
                if(name.contains("Main")) {
                    Main ma = ((Main) fragments.get(mViewPager.getCurrentItem()));
                if(ma.mActionMode!=null){ma.mActionMode.finish();ma.mActionMode=null;}
                } else if(name.contains("ZipViewer")){
                    ZipViewer ma = ((ZipViewer) fragments.get(mViewPager.getCurrentItem()));

                    if(ma.mActionMode!=null){ma.mActionMode.finish();ma.mActionMode=null;}}

            }

            public void onPageSelected(int p1) { // TODO: Implement this								// method
               currenttab=p1;
                mainActivity.supportInvalidateOptionsMenu();
                try {
                    updateSpinner();
                } catch (Exception e) {
                   // e.printStackTrace();
                }
                String name=fragments.get(p1).getClass().getName();
                if(name.contains("Main")){
                    Main ma = ((Main) fragments.get(p1));
                if (ma.current != null) {
                    try {mainActivity.updateDrawer(ma.current);
                    ma.updatePath(ma.current);
                    if(ma.buttons.getVisibility()==View.VISIBLE)ma.bbar(ma.current);

                        ((TextView) STRIP.getChildAt(p1)).setText(ma.current);
                    } catch (Exception e) {
                 //       e.printStackTrace();
                    }
                }}
                else if(name.contains("ZipViewer")){ZipViewer ma = ((ZipViewer) fragments.get(p1));
                    try {
                        ma.bbar();
                    } catch (Exception e) {
                   //     e.printStackTrace();
                    }
                }
            }

            public void onPageScrollStateChanged(int p1) {
                // TODO: Implement this method
            }
        });
        if (savedInstanceState == null) {
            mSectionsPagerAdapter = new ScreenSlidePagerAdapter(
                    getActivity().getSupportFragmentManager());
            TabHandler tabHandler=new TabHandler(getActivity(),null,null,1);
            List<Tab> tabs1=tabHandler.getAllTabs();
            int i=tabs1.size();
            if(i==0){
                addTab(path0);addTab(path1);
            } else {
                for(Tab tab:tabs1){addTab(tab.getPath());
                }
            }

            if(path!=null && path.trim().length()>0)
            {
                addTab1(path);
            }
            mViewPager.setAdapter(mSectionsPagerAdapter);
            if(path!=null && path.trim().length()!=0){
                mViewPager.setCurrentItem(fragments.size()-1);
            }
            else {
          int k=Sp.getInt("currenttab",0);
            try {
                mViewPager.setCurrentItem(k,true);
            } catch (Exception e) {
                e.printStackTrace();
            }}

          if(zippath!=null && zippath.trim().length()>0)addZipViewerTab(zippath);
        } else {
            fragments.clear();
          tabs= savedInstanceState.getStringArrayList("tabs");
            for(int i=0;i<tabs.size();i++){
                fragments.add(i, getActivity().getSupportFragmentManager().getFragment(savedInstanceState, "tab"+i));
            }
            mSectionsPagerAdapter = new ScreenSlidePagerAdapter(
                    getActivity().getSupportFragmentManager());

            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        updatepaths();
    }
    public void updatepaths(){
        TabHandler
                tabHandler = new TabHandler(getActivity(), null, null, 1);
        int i=0;
        tabHandler.clear();for(Fragment fragment:fragments){
            if(fragment.getClass().getName().contains("Main")){
                Main m=(Main)fragment;
                tabHandler.addTab(new Tab(i,m.current,m.current));i++;
            }}
        Sp.edit().putInt("currenttab",currenttab).apply();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int i = 0;
        if (fragments != null && fragments.size() !=0){
            for (Fragment fragment : fragments) {
                getActivity().getSupportFragmentManager().putFragment(outState, "tab" + i, fragment);
                i++;
            }
            outState.putStringArrayList("tabs",tabs);
        }
    }
    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        @Override
        public int getItemPosition (Object object)
        {
            int index = fragments.indexOf ((Fragment)object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                String name=fragments.get(position).getClass().getName();
                if(name.contains("Main")){
                Main ma = ((Main) fragments.get(position));
                if (ma.results) {
                    return utils.getString(getActivity(), R.string.searchresults);
                } else {
                    if (ma.current.equals("/")) {
                        return "Root";
                    } else {
                        return new File(ma.current).getName();
                    }
                }}else if(name.contains("ZipViewer")) {
                    ZipViewer ma = ((ZipViewer) fragments.get(position));

                    try {
                        return ma.f.getName();
                    } catch (Exception e) {
                        return "ZipViewer";
                      //  e.printStackTrace();
                    }

                }
                return fragments.get(position).getClass().getName();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        public int getCount() {
            // TODO: Implement this method
            return fragments.size();
        }

        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment f;
            f = fragments.get(position);
            return f;
        }

    }


    public void addTab(String text) {
        android.support.v4.app.Fragment main = new Main();
        int p = fragments.size();

        if (text != null && text.trim().length() != 0) {
            Bundle b = new Bundle();
            b.putString("path", text);
            main.setArguments(b);
        }
        fragments.add(main);
        tabs.add(main.getClass().getName());
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setOffscreenPageLimit(fragments.size()+1);
    }
    public void addTab1(String text) {
        android.support.v4.app.Fragment main = new Main();

        if (text != null && text.trim().length() != 0) {
            Bundle b = new Bundle();
            b.putString("path", text);
            main.setArguments(b);
        }else {
            Bundle b = new Bundle();
            b.putString("tag", mViewPager.getCurrentItem()+1+"");
            main.setArguments(b);
        }
        fragments.add(main);
        tabs.add(main.getClass().getName());
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(fragments.size()-1,true);

        mViewPager.setOffscreenPageLimit(fragments.size()+1);
    }

    public void removeTab(){

        int i=mViewPager.getCurrentItem();
        mSectionsPagerAdapter=null;
        mViewPager.setAdapter(null);
        fragments.remove(i);
        mSectionsPagerAdapter = new ScreenSlidePagerAdapter(
                getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        if(i>0) {
            mViewPager.setCurrentItem(i-1,true);
        } else if(i==0 && fragments.size()>2) {
            mViewPager.setCurrentItem(i+1,true);
        }
        updateSpinner();
        mViewPager.setOffscreenPageLimit(fragments.size()+1);
        updatepaths();
    }
    public void addZipViewerTab(String text) {
        android.support.v4.app.Fragment main = new ZipViewer();
        int p = fragments.size();

        if (text != null && text.trim().length() != 0) {
            Bundle b = new Bundle();
            b.putString("path", text);
            main.setArguments(b);
        }
        fragments.add(main);
        tabs.add(main.getClass().getName());
        mSectionsPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(fragments.size()-1,true);

    }
    public Fragment getTab() {
        return fragments.get(mViewPager.getCurrentItem());
    }
    public Fragment getTab1() {
        Fragment man = ( fragments.get(mViewPager.getCurrentItem()));
        return man;
    }
    public void updateSpinner(){
        ArrayList<String> items=new ArrayList<String>();
        for(Fragment fragment:fragments){
            String name=fragment.getClass().getName();
            if(name.contains("Main")){items.add(((Main)fragment).current);
        }else if(name.contains("ZipViewer")){String cu=((ZipViewer)fragment).f.getName();items.add(cu);}}
        tabSpinnerAdapter=new TabSpinnerAdapter(mainActivity.getSupportActionBar().getThemedContext(), R.layout.rowlayout,items,mainActivity.tabsSpinner,this);
        mainActivity.tabsSpinner.setAdapter(tabSpinnerAdapter);
        mainActivity.tabsSpinner.setSelection(mViewPager.getCurrentItem());
    }
}
