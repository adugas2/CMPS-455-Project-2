package com.company;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class MyThreadCL extends Thread {
    private int id;
    private static int numDomains;
    private static int numObjects;
    private int domain;
    private int requests = 5;
    private static String[] domainList;
    private Semaphore[] mutex;
    private static String[] objects;


    public MyThreadCL(int id, int numDomains, int numObjects, int domain, String[] domainList, Semaphore[] mutex, String[] objects){
        super(String.valueOf(id));
        this.id = id;
        this.numDomains = numDomains;
        this.numObjects = numObjects;
        this.domain = domain;
        this.domainList = domainList;
        this.mutex = mutex;
        this.objects = objects;
    }

    public static void arbitrator(int id, int domain, int request, int action){
        //read
        if (action == 0) {
            System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Attempting to read from resource: F" + (request+1));
            if (domainList[domain].contains("D" + (domain+1) + ":R")) {
                System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Resource F" + (request+1) + " contains " + objects[request].toString());
            } else {
                System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Read failed, permission denied");
            }
            //write
        } else {
            System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Attempting to write to resource: F" + (request+1));
            if (domainList[domain].contains("D" + (domain+1) + ":R/W") || domainList[domain].contains("D" + (domain+1) + ":W")) {
                System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Writing ThreadID to resource: F" + (request+1));
                objects[request] = Integer.toString(id);
            } else {
                System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Write failed, permission denied");
            }
        }
    }
    public static void arbitrator(int id, int domain, int request){
        int switchNum = numDomains + numObjects - request - 1;
        System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Attempting to switch from D" + (domain+1) + " to D" + (switchNum+1));
        if (domainList[domain].contains("D" + domain + ":allow")) {
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
                try {
                    mutex[request].acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                arbitrator(id, domain, request, action);
                for (int j = 0; j < r.nextInt(5) + 3; j++) {
                    MyThreadMatrix.yield();
                }
                mutex[request].release();
            }else{
                //Domain Switch request
                while (numDomains + numObjects - request - 1 == domain){
                    //gen new request # if trying to switch into own domain
                    request = r.nextInt(numDomains) + numObjects;
                }
                arbitrator(id, domain, request);
            }
            requests--;

            //yielding
            int yield = r.nextInt(5) + 3;
            System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] yielding for " + yield + " cycles");
            for (int j = 0; j < yield; j++) {
                MyThreadMatrix.yield();
            }
        }
    }
}

