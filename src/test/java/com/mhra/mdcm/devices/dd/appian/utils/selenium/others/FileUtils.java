package com.mhra.mdcm.devices.dd.appian.utils.selenium.others;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author TPD_Auto
 */
public class FileUtils {

    private final static String resourceFolder = "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    public final static String userFileLocation = "configs" + File.separator;

    private final static Map<String, Properties> mapOfProperties = new HashMap<String, Properties>();

    /**
     * Load properties files from system
     *
     * @param fileName
     * @return
     */
    public static Properties loadPropertiesFile(String fileName) {

        Properties prop = mapOfProperties.get(fileName);
        if (prop == null) {
            try {
                String root = new File("").getAbsolutePath();
                String location = root + File.separator + resourceFolder + userFileLocation + fileName;
                prop = new Properties();
                InputStream in = new FileInputStream(new File(location));
                prop.load(in);
                in.close();

                //update map
                mapOfProperties.put(fileName, prop);
            } catch (Exception e) {
                prop = null;
                e.printStackTrace();
            }
        }

        return prop;
    }


    public static Properties getEnvironmentConfigProperties() {
        String testUrl = "https://mhratest.appiancloud.com";
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null || profile.trim().equals("")) {
            profile = "mhratest";
        }

        System.out.println("TEST RUNNING IN : " + profile );
        Properties props = FileUtils.loadPropertiesFile("envs" + File.separator + profile + ".properties");
        return props;
    }


    public static String getTestUrl() {
        String testUrl = "https://mhratest.appiancloud.com";
        Properties props = getEnvironmentConfigProperties();
        String baseUrl = props.getProperty("base.url");
        if (baseUrl != null) {
            testUrl = baseUrl;
        }

        //Giving me 401 error message : Appian said to use https://mhratest.appiancloud.com/suite/portal/loginPage.none
        testUrl = testUrl + "/suite/portal/login.jsp";
        System.out.println("URL : " + testUrl);
        
        return testUrl;
    }


    public static String getSpecificPropertyFromFile(String fileName, String property) {
        Properties prop = loadPropertiesFile(fileName);
        String o = prop.getProperty(property);
        return o;
    }

    /**
     * Get full path to a specific file related to root of project
     *
     * @param fileName
     * @return
     */
    public static String getDataFileFullPath(String fileName) {
        File file = new File("");
        String rootFolder = file.getAbsolutePath();
        String data = (rootFolder + File.separator + resourceFolder + fileName);
        return data;
    }

    /**
     * @return
     */
    public static String getTempFileFullPath() {
        File file = new File("");
        String rootFolder = file.getAbsolutePath();
        String data = (rootFolder + File.separator + resourceFolder + "tmp");
        return data;
    }


    public static String getFileFullPath(String tmpFolderName, String fileName) {
        File file = new File("");
        String rootFolder = file.getAbsolutePath();
        String data = (rootFolder + File.separator + resourceFolder + tmpFolderName + File.separator + fileName);
        return data;
    }


    /**
     * If no xmlFileName is supplied than it defaults to Text.xml
     *
     * @param xmlFileName
     * @return
     */
    public static String getXMLNotificationDataFileName(String xmlFileName) {
        boolean randomOrEmpty = false;
        if (xmlFileName != null && (xmlFileName.contains(".xml") || xmlFileName.trim().equals("random"))) {
            if (xmlFileName.trim().equals("") || xmlFileName.trim().equals("random")) {
                randomOrEmpty = true;
                if (xmlFileName.equals("random"))
                    xmlFileName = "Test" + new Date().toString().substring(0, 17).replaceAll(" ", "").replace(":", "") + ".xml";
                else
                    xmlFileName = "Test.xml";
            }
        } else {
            randomOrEmpty = true;
            xmlFileName = "Test.xml";
        }

        if(!randomOrEmpty){
            if(xmlFileName.contains(".")) {
                String data[] = xmlFileName.split("\\.");
                xmlFileName = data[0] + "_" + RandomDataUtils.getTimeMinHour(true) + "." + data[1];
            }
        }
        return xmlFileName;
    }


    public static Properties driverProp;

    public static String getASpecificDriverProperty(String propertyName, String fileLocationAndName) {
        String value = null;
        if (driverProp == null) {
            try {
                String root = new File("").getAbsolutePath();
                String location = root + File.separator + resourceFolder + fileLocationAndName;
                driverProp = new Properties();
                InputStream in = new FileInputStream(new File(location));
                driverProp.load(in);
                in.close();

            } catch (Exception e) {
                driverProp = null;
                e.printStackTrace();
            }
        }else{
            value = driverProp.getProperty(propertyName);
        }
        return value;
    }

    public static void deleteFile(String zipFile) {
        File f = new File(zipFile);
        f.delete();
    }

    /**
     * If we need to override the default user name for running the tests
     *
     * By default its the Automation usernames
     * @param overrideUsername
     * @param uname
     * @return username key from the properties file
     *  one of : business+uname, manufacturer+uname, authorisedrep+uname
     */
    public static String getOverriddenUsername(String overrideUsername, String uname) {
        if(overrideUsername!=null && !overrideUsername.equals("")){
            if(uname.contains("business")){
                uname = "business" + overrideUsername;
            }else if(uname.contains("manufacturer")){
                uname = "manufacturer" + overrideUsername;
            }else if(uname.contains("authorised")){
                uname = "authorisedrep" + overrideUsername;
            }
        }
        return uname;
    }



    public static String getTargetFileFullPath(String folderName, String fileName) {
        File file = new File("");
        String rootFolder = file.getAbsolutePath();
        String data = (rootFolder + File.separator + folderName + File.separator + fileName);
        return data;
    }
}
