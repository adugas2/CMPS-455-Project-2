package com.company;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class MyThreadMatrix extends Thread{

    static String id;
    static int numDomains;
    static int numObjects;
    static int domain;
    int requests = 5;
    static String[][] matrix;
    static Semaphore[] mutex;
    static Semaphore[] wrt;


    public MyThreadMatrix(int id, int numDomains, int numObjects, int domain, String[][] matrix, Semaphore[] mutex, Semaphore[] wrt){
        super(String.valueOf(id));
        this.id = String.valueOf(id);
        this.numDomains = numDomains;
        this.numObjects = numObjects;
        this.domain = domain;
        this.matrix = matrix;
        this.mutex = mutex;
        this.wrt = wrt;
    }

    public static void arbitrator(int reequest, int action){

    }
    public static void arbitrator(int request){
        int switchNum = numDomains + numObjects - request;
        System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Attempting to switch from D" + (domain+1) + " to D" + (switchNum+1));
        if (matrix[domain][switchNum + numObjects].equals("allow")) {
            domain = switchNum;
            System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Successfully switched to D" + (domain+1));
        } else {
            System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Domain Switch failed, permission denied");
        }
    }

    @Override
    public void run() {
        Random r = new Random();
        while (requests > 0) {
            //generating random request
            int request = r.nextInt(numDomains + numObjects);

            if (request < numObjects){
            //R/W request
                int action = r.nextInt(2);


            }else{
            //Domain Switch request
                while (numDomains + numObjects - request == domain){
                    //gen new request # if trying to switch into own domain
                    request = r.nextInt(numDomains) + numObjects;
                }
                arbitrator(request);
            }
            requests--;
            System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] yielding");
            for (int j = 0; j < r.nextInt(4) + 3; j++) {
                MyThreadMatrix.yield();
            }
        }
    }
}
