package com.yuqiaotech.preferences;


import com.yuqiaotech.app.AppWatchApplication;

public class AppInfoPreferences extends PcAbsPreferences {

    private static final String FILE_NAME = "app_info";
    public static final String SP_PACKAGE_NAME = "SP_PACKAGE_NAME";

    public AppInfoPreferences() {
        super(AppWatchApplication.getApplication(), FILE_NAME);
    }

    public void putPackageName(String packageName) {
        putString(SP_PACKAGE_NAME, packageName);
        commit();
    }


    public String getPackageName(String  defalutVal) {
        return getString(SP_PACKAGE_NAME, defalutVal);
    }

    public String removedPackageName( ) {
        String packagename = getString(SP_PACKAGE_NAME, "");
        removeKey(SP_PACKAGE_NAME);
        commit();
        return packagename;
    }

    /**
     * 清空
     */
    public void Clear() {
        if (null == mPreferences) return;

        if (null == mEditor) {
            mEditor = mPreferences.edit();
        }
        mEditor.clear();
        mEditor.commit();
    }

}
