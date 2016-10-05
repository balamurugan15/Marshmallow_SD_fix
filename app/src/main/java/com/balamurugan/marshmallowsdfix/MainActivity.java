package com.balamurugan.marshmallowsdfix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    String tempFile;
    String pkgname;

    private AdView mAdView;


    FileOutputStream outputStream;
    FileInputStream inputStream;

   // String FilePath = "/data/user/0/com.balamurugan.marshmallowsdfix/files/FileName.xml";
    String FilePath;
    private List<ListItem> appList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyListAdapter mAdapter;

    private ProgressBar bar;

    File theSharedPrefsFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
               // .addTestDevice("851FAD29862B8E8FF51B83F3B0909E17")
                .build();
        mAdView.loadAd(adRequest);

     /*   mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(getApplicationContext(), "Ad is loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                Toast.makeText(getApplicationContext(), "Ad is opened!", Toast.LENGTH_SHORT).show();
            }
        }); */

        bar = (ProgressBar) this.findViewById(R.id.marker_progress);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MyListAdapter(appList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);




        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ListItem listItem = appList.get(position);


                if(listItem.isSelected()){
                    RemovePackage task = new RemovePackage();

                    task.packname = listItem.getPkgName();
                    task.execute();
                    listItem.setSelected(false);
                }
                else {
                    AddPackage task = new AddPackage();
                    task.packname = listItem.getPkgName();
                     task.execute();
                    listItem.setSelected(true);
                }
                appList.clear();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        outputStream = null;

        File temp = new File(getFilesDir() + "/NewFile.xml" );


        String content = "<sdfix />\n";

        SharedPreferences pref = getApplicationContext().getSharedPreferences("boolean", 0); // 0 - for private mode

        if(!pref.contains("firstlaunch")) {
            try {

                //inputStream = new FileInputStream(getFilesDir() + "/NewFile.xml");
                //Document doc = documentBuilder.parse(inputStream);

                outputStream = openFileOutput("NewFile.xml", MODE_WORLD_READABLE);
                outputStream.write(content.getBytes());
                outputStream.close();
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("firstlaunch", true);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new LoadView().execute();




    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }



    private class LoadView extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //my stuff is here
            populateView();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            bar.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }




/////////////// Make local copy, open and read xml

    private void openXml(){

        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
           // os.writeBytes("mount -ro remount,rw /system\n");
            os.writeBytes("cp -r /data/system/packages.xml " + getFilesDir() + "/temp.xml\n");
            os.writeBytes("chmod 777 " + getFilesDir() + "/temp.xml\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        tempFile = getFilesDir() + "/temp.xml";


    }




/////////////// Populate recycler view

    void populateView(){


           // openXml();

            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            Boolean isSelected = false;
            Drawable icon = null;
            String applicationName = null;



            List<PackageInfo> apps = getPackageManager().getInstalledPackages(0);

            for(int i=0;i<apps.size();i++) {
                PackageInfo p = apps.get(i);
                if((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                    continue;
                }
                applicationName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                pkgname = p.packageName;
                icon = p.applicationInfo.loadIcon(getPackageManager());

                isSelected = checkTick(pkgname);

                ListItem item = new ListItem(pkgname, applicationName,icon, isSelected );
                appList.add(item);
            }
    }

    boolean checkTick(String pname){

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            inputStream = new FileInputStream(getFilesDir() + "/NewFile.xml");
            Document doc = documentBuilder.parse(inputStream);

            Element myappTag = doc.getDocumentElement();

        Element packagesTag = (Element) myappTag.getElementsByTagName(pname).item(0);

        return packagesTag != null ? true : false;

    } catch (ParserConfigurationException pce) {
        pce.printStackTrace();
    }catch (IOException ioe) {
        ioe.printStackTrace();
    } catch (SAXException sae) {
        sae.printStackTrace();
    }
        return false;
    }


    void modifyXml(String pname){

        try {


            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(new File(tempFile));

            Element packagesTag = doc.getDocumentElement();
            packagesTag.getTagName();
            NodeList packageList =  packagesTag.getElementsByTagName("package");

            for (int i = 0; i < packageList.getLength(); i++) {
                Node node = packageList.item(i);
                Element elem = (Element)node;
                pkgname = elem.getAttribute("name");

                if(pname.equals(pkgname)){
                    //Element packagesTag = doc.getDocumentElement();
                    Element perms =  (Element)elem.getElementsByTagName("perms").item(0);
                    Element wms = doc.createElement("item");
                    wms.setAttribute("name", "android.permission.WRITE_MEDIA_STORAGE");
                    wms.setAttribute("granted", "true");
                    wms.setAttribute("flags", "0");
                    // Configure the message element
                    perms.appendChild(wms);
                }
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(tempFile));
            transformer.transform(source, result);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }catch (TransformerException tfe) {
            tfe.printStackTrace();
        }


      //  moveToData();


    }





/////////////// Implement OnClick()



    private class AddPackage extends AsyncTask<Void, Void, Boolean> {

        String packname;

        @Override
        protected void onPreExecute(){
           // bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //my stuff is here
            writeToFile(packname);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(getApplicationContext(), "Reboot device for changes to take effect", Toast.LENGTH_LONG).show();
            new LoadView().execute();
        }
    }



    private class RemovePackage extends AsyncTask<Void, Void, Boolean> {

        String packname;

        @Override
        protected void onPreExecute(){
            // bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //my stuff is here
            removeFromFile(packname);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Toast.makeText(getApplicationContext(), "Reboot device for changes to take effect", Toast.LENGTH_LONG).show();
            new LoadView().execute();
        }
    }







    void writeToFile(String pname){

        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = null;
            inputStream = new FileInputStream(getFilesDir() + "/NewFile.xml");
            doc = documentBuilder.parse(inputStream);
           /* File myFile = new File(FilePath);
            if(myFile.exists()) {
                if (myFile.canRead()) {
                    if(myFile.canWrite()) {
                       // doc = documentBuilder.parse(new File(getCacheDir() + "my.xml"));
                        doc = documentBuilder.parse(myFile);
                    }
                }
            } */
            Element elem = doc.createElement(pname);

            Element myappTag = doc.getDocumentElement();
            myappTag.appendChild(elem);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            outputStream = openFileOutput("NewFile.xml", MODE_WORLD_READABLE);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }


    void removeFromFile(String pname){

        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document doc = null;
            inputStream = new FileInputStream(getFilesDir() + "/NewFile.xml");
            doc = documentBuilder.parse(inputStream);

            Element myappTag = doc.getDocumentElement();

            Element appTag = (Element) myappTag.getElementsByTagName(pname).item(0);

            if(appTag.getParentNode() != null){
                appTag.getParentNode().removeChild(appTag);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            outputStream = openFileOutput("NewFile.xml", MODE_WORLD_READABLE);
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        }catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){

            case R.id.aboutmenu:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.reboot:
                rebootNow();
                break;

        }


    return true;
    }

    void rebootNow(){

        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("reboot\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }



    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }




}