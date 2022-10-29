package com.company;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {
        Task2();
    }

    public static void Task1(){
        System.out.println("Access control scheme: Access Matrix");
        //initializing variables
        Random r = new Random();
        // this is N
        int numDomains = r.nextInt(5) + 3;
        System.out.println("Domain Count: " + numDomains);

        //This is M
        int numObjects = r.nextInt(5) + 3;
        System.out.println("Object Count: " + numObjects);

        //creating and populating access matrix
        int access;
        String [][] matrix = new String [numDomains][numDomains + numObjects];
        //populating permissions for objects
        for (int i = 0; i < numDomains; i++){
            for (int j = 0; j < numObjects; j++){
                access = r.nextInt(4);
                switch (access) {
                    case(0):
                        matrix[i][j] = "   ";
                        break;
                    case(1):
                        matrix[i][j] = "R  ";
                        break;
                    case(2):
                        matrix[i][j] = "W  ";
                        break;
                    case(3):
                        matrix[i][j] = "R/W";
                        break;
                }
            }
        }
        //populating permissions for domain switching
        for (int i = 0; i < numDomains; i++){
            for (int j = numObjects; j < numDomains + numObjects; j++){
                access = r.nextInt(2);
                if (j == i+numObjects){
                    access = 0;
                }
                if (access == 0){
                    matrix[i][j] = "     ";
                } else {
                    matrix[i][j] = "allow";
                }
            }
        }

        //printing matrix
        System.out.println();
        System.out.print("     ");
        for(int i = 0; i < numDomains + numObjects; i++){
            if (i < numObjects) {
                System.out.print(" F" +(i+1)+ "  ");
            } else {
                System.out.print(" D" + (i - numObjects + 1) + "    ");
            }
        }
        System.out.println();
        int domaincount = 1;
        for (String[] row : matrix) {
            System.out.print("D" + domaincount + ": ");
            System.out.println(Arrays.toString(row));
            domaincount++;
        }
        System.out.println();

        //setting up semaphores and objects
        Semaphore[] mutex = new Semaphore[numObjects];
        for (int i = 0; i < mutex.length; i++)
            mutex[i] = new Semaphore(1);
        Semaphore[] wrt = new Semaphore[numObjects];
        String[] objects = new String[numObjects];
        Arrays.fill(objects, new String("null"));

        //setting up and starting threads
        Thread[] threads = new Thread[numDomains];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThreadMatrix(i, numDomains, numObjects, i, matrix, mutex, wrt, objects);
        }
        for (int i = 0; i < threads.length; i++){
            threads[i].start();
        }
    }
    public static void Task2() {

        System.out.println("Access control scheme: Access List");
        //initializing variables
        Random r = new Random();
        // this is N
        int numDomains = r.nextInt(5) + 3;
        System.out.println("Domain Count: " + numDomains);

        //This is M
        int numObjects = r.nextInt(5) + 3;
        System.out.println("Object Count: " + numObjects);

        int access;
        String[] objectList = new String[numObjects + numDomains];
        for (int i = 0; i < objectList.length; i++){
            objectList[i] = "";
        }

        for (int i = 0; i < numDomains; i++) {
            for (int j = 0; j < numObjects; j++) {
                access = r.nextInt(4);
                int domain = i+1;

                String permission = "";
                switch (access) {
                    case(0):
                        permission = "   ";
                        break;
                    case(1):
                        permission = "R  ";
                        objectList[j] = objectList[j].concat("D" + String.valueOf(domain) +":" + permission + ", ");
                        break;
                    case(2):
                        permission = "W  ";
                        objectList[j] = objectList[j].concat("D" + String.valueOf(domain) +":" + permission + ", ");
                        break;
                    case(3):
                        permission = "R/W";
                        objectList[j] = objectList[j].concat("D" + String.valueOf(domain) +":" + permission + ", ");
                        break;
                }
            }
        }

        for (int i = 0; i < numDomains; i++) {
            for (int j = numObjects; j < numDomains + numObjects; j++) {
                access = r.nextInt(2);
                if (j == i + numObjects) {
                    access = 0;
                }
                if (access == 0) {
                    //objectList[j] = objectList[j].concat("D" + (i+1) +":" + "     " + ", ");
                }
                else {
                    objectList[j] = objectList[j].concat("D" + (i+1) +":" + "allow" + ", ");
                }
            }
        }

        System.out.println();
        for (int k = 0; k < numDomains + numObjects; k++) {
            if (k < numObjects) {
                System.out.println("F" + (k+1) + "--> " +objectList[k]);
            } else {
                System.out.println("D" + (k-numObjects+2) + "--> " +objectList[k]);
            }
        }

        System.out.println();

        Semaphore[] mutex = new Semaphore[numObjects];
        for (int i = 0; i < mutex.length; i++) {
            mutex[i] = new Semaphore(1);
        }

        Thread[] threads = new Thread[numDomains];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThreadACL(i, numDomains, numObjects, i, objectList, mutex, objectList);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
    }
}
