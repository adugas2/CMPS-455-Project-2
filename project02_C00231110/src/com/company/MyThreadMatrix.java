package com.company;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class MyThreadMatrix extends Thread{

    private int id;
    private static int numDomains;
    private static int numObjects;
    private int domain;
    private int requests = 5;
    private static String[][] matrix;
    private Semaphore[] mutex;
    private Semaphore[] wrt;
    private static String[] objects;


    public MyThreadMatrix(int id, int numDomains, int numObjects, int domain, String[][] matrix, Semaphore[] mutex, Semaphore[] wrt, String[] objects){
        super(String.valueOf(id));
        this.id = id;
        this.numDomains = numDomains;
        this.numObjects = numObjects;
        this.domain = domain;
        this.matrix = matrix;
        this.mutex = mutex;
        this.wrt = wrt;
        this.objects = objects;
    }

    public static void arbitrator(int id, int domain, int request, int action){
            //read
            if (action == 0) {
                System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Attempting to read from resource: F" + (request+1));
                if (matrix[domain][request].contains("R")) {
                    System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Resource F" + (request+1) + " contains " + objects[request].toString());
                } else {
                    System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Read failed, permission denied");
                }
            //write
            } else {
                System.out.println("[Thread: " + id + "(D" + (domain+1) + ")] Attempting to write to resource: F" + (request+1));
                if (matrix[domain][request].contains("W")) {
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
                try {
                    mutex[request].acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                arbitrator(id, domain, request, action);
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
