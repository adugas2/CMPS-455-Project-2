package com.company;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {
        Task1();
    }

    public static void Task1(){
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
        int count = 1;
        for (String[] row : matrix) {
            System.out.print("D" + count + ": ");
            System.out.println(Arrays.toString(row));
            count++;
        }

    }

}
