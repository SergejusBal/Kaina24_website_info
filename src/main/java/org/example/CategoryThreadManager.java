package org.example;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CategoryThreadManager {
    private static CopyOnWriteArrayList<String> categoryList;

    public static List<String> initializeManager(){
        if(categoryList != null) return categoryList;

        MySQLService mySQLService = new MySQLService();
        List<String> databaseList = mySQLService.getAllURLs();
        categoryList = new CopyOnWriteArrayList<>(databaseList);
        return categoryList;
    }

    public static synchronized String getURL(){
        if(categoryList == null) return "Initialize manager first!";
        if(categoryList.isEmpty()) return "";

        String url = categoryList.getFirst();
        categoryList.removeFirst();
        return url;
    }

    public static synchronized Integer getSize(){
        if(categoryList == null) return null;
        if(categoryList.isEmpty()) return 0;
        return categoryList.size();
    }


}
