package org.example;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

//        URLCategoryScanner categoryScanner = new URLCategoryScanner();
//        categoryScanner.scanForCategories();

        CategoryThreadManager.initializeManager();

        for(int i = 0; i < CategoryThreadManager.getSize(); i++) {
            ReadProductFromCategory readProductFromCategory = new ReadProductFromCategory(CategoryThreadManager.getURL());
            Thread thread = new Thread(readProductFromCategory);
            thread.run();
        }



    }
}