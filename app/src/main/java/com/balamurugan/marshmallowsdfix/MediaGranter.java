package com.balamurugan.marshmallowsdfix;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Balamurugan M on 6/21/2016.
 */


public class MediaGranter implements IXposedHookLoadPackage {

    public static final String TAG = "MarshmallowSDFixe:mod";
    public static final boolean DEBUG = false;

    private static final String CLASS_PACKAGE_MANAGER_SERVICE = "com.android.server.pm.PackageManagerService";
    private static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";

    private static final String PERM_MEDIA_STORAGE = "android.permission.WRITE_MEDIA_STORAGE";



    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }





    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("android"))
            return;

        final Class<?> pmServiceClass = XposedHelpers.findClass(CLASS_PACKAGE_MANAGER_SERVICE, lpparam.classLoader);
        findAndHookMethod(pmServiceClass, "grantPermissionsLPw",
                CLASS_PACKAGE_PARSER_PACKAGE, boolean.class, String.class, new XC_MethodHook(){
               // findAndHookMethod(CLASS_PACKAGE_MANAGER_SERVICE, lpparam.classLoader, "grantPermissionsLPw", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // this will be called before the clock was updated by the original method
            }
            @SuppressWarnings("unchecked")
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                //log("Hooked....");
                final String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");
                //log("pack = " + pkgName + "....");

                InputStream inputStream = new FileInputStream("/data/user/0/com.balamurugan.marshmallowsdfix/files/NewFile.xml");
                //log("file opn....");

                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document doc = documentBuilder.parse(inputStream);

                Element myappTag = doc.getDocumentElement();

                //log("Patched :D .....");

                Element appTag = (Element) myappTag.getElementsByTagName(pkgName).item(0);



                // MM-SDfix
                if (appTag != null) {
                    final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
                    final Object ps = XposedHelpers.callMethod(extras, "getPermissionsState");
                    final List<String> grantedPerms =
                            (List<String>) XposedHelpers.getObjectField(param.args[0], "requestedPermissions");
                    final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                    final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                    // Add android.permission.WRITE_MEDIA_STORAGE needed by screen recorder
                    if (!(boolean)XposedHelpers.callMethod(ps,"hasInstallPermission", PERM_MEDIA_STORAGE)) {
                        final Object pAccessSurfaceFlinger = XposedHelpers.callMethod(permissions, "get",
                                PERM_MEDIA_STORAGE);
                        int ret = (int) XposedHelpers.callMethod(ps, "grantInstallPermission", pAccessSurfaceFlinger);

                        log("Patched....");
                        if (DEBUG) log("Permission added: " + pAccessSurfaceFlinger + "; ret=" + ret);
                    }


                    if (DEBUG) {
                        log("List of permissions: ");
                        for (String perm : grantedPerms) {
                            log(pkgName + ": " + perm);
                        }
                    }
                }
            }
        });
    }
}
