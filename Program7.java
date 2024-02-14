//********************************************************************
//
//  Author:        Marshal Pfluger
//
//  Program #:     Seven
//
//  File Name:     Program7.java
//
//  Course:        COSC 4302 Operating Systems
//
//  Due Date:      11/17/2023
//
//  Instructor:    Prof. Fred Kumi
//
//  Java Version:  17
//
//  Description:   MultiThreaded program that simulates a one lane bridge and prevents deadlock
//
//********************************************************************

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Program7 {
	public static AtomicInteger bridgeCount = new AtomicInteger();
	
	private final Semaphore northLock = new Semaphore(1);
	private final Semaphore southLock = new Semaphore(1);
	private final Semaphore bridgeLock = new Semaphore(1);
	
	private Random random = new Random();


	
	public static void main (String[] args) {
		Program7 obj = new Program7();
		obj.developerInfo();
		obj.runDemo();
	}
	
	
    //***************************************************************
    //
    //  Method:       runDemo (Non Static)
    // 
    //  Description:  driver to test the program
    //
    //  Parameters:   None
    //
    //  Returns:      N/A 
    //
    //**************************************************************
	public void runDemo() {

    	// Declare variables for operation 
    	String sentinel = "";
    	
    	// Set processors to num of machine minus one for main thread
    	int numThreads = numProcessors() * 2;
    	
    	
    	// Loop through functionality to rerun program operations
    	do {
  
    		// Create thread array
    		Thread[] threads = threadAllocator(numThreads);
    		
    		// join threads to wait for completion
    		joinThreads(threads, numThreads);

            // Allow user to rerun the program
            printOutput("Would you like to run the program again?\n"
            		  + "Press '0' to exit or enter key to re run.");
            // Set sentinel value to the users choice
            sentinel = userChoice();
    	} 
    	while (!sentinel.equals("0"));
    	
    	// Inform user the program has terminated
    	printOutput("The program has terminated, have a good day.");

    }
	
    //***************************************************************
    //
    //  Method:       joinThreads()
    // 
    //  Description:  calls .join on the array of threads to wait for them to exit program
    //
    //  Parameters:   None
    //
    //  Returns:      N/A 
    //
    //**************************************************************
	public void joinThreads(Thread[] threads, int numThreads) {
        // Join the threads to wait for completion 
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	
    //***************************************************************
    //
    //  Method:       threadAllocator()
    // 
    //  Description:  Creates and starts new threads for the program
    //
    //  Parameters:   None
    //
    //  Returns:      N/A 
    //
    //**************************************************************
	public Thread[] threadAllocator(int numThreads) {

		
		Thread[] threads = new Thread[numThreads];
		
        // Start threads for number of threads, split points between threads
        for (int i = 0; i < numThreads; i++) {
        	
        	// Generate a random number to randomize directions
        	int directionRandomizer = (random.nextInt(2) + 1);
        	
        	// Add new thread to the array of threads: pass in the direction, number, locks
            threads[i] = new Thread(new TravelerThread((directionRandomizer==1?"SouthBound":"NorthBound"), i + 1, northLock, southLock, bridgeLock));
            
            // Start the thread at current element
            threads[i].start();
            
            try {
            	// Sleep thread to simulate farmers arriving at different times
				Thread.sleep(random.nextInt(100, 700));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		return threads;
	}
		
	//***************************************************************
	//
	//  Method:       numProcessors
	// 
	//  Description:  Sets the number of processors for the machine
	//
	//  Parameters:   String output
	//
	//  Returns:      N/A
	//
	//***************************************************************
    public int numProcessors() {
		// Get count of available cores
		return Runtime.getRuntime().availableProcessors();
    }
   
    //**************************************************************
    //
    //  Method:       userChoice
    //
    //  Description:  gets input from user, closes scanner when program exits 
    //
    //  Parameters:   N/A
    //
    //  Returns:      String file
    //
    //**************************************************************	
    public String userChoice() {
    	String userChoice;
    	// Use Scanner to receive user input
    	Scanner userInput = new Scanner(System.in);
    	// Store user choice
    	userChoice = userInput.nextLine();
    	
    	// close scanner when program exits.
    	if (userChoice.equalsIgnoreCase("0")) {
    		userInput.close();
    		}
    	return userChoice;
    	}
    
	//***************************************************************
	//
	//  Method:       printOutput (Non Static)
	// 
	//  Description:  handles printing output
	//
	//  Parameters:   String output
	//
	//  Returns:      N/A
	//
	//***************************************************************
	public void printOutput(String output) {
		//Print the output to the terminal
		System.out.print("\n");
		System.out.println(output);
	}//End printOutput
	
    //***************************************************************
    //
    //  Method:       developerInfo (Non Static)
    // 
    //  Description:  The developer information method of the program
    //
    //  Parameters:   None
    //
    //  Returns:      N/A 
    //
    //**************************************************************
    public void developerInfo(){
       System.out.println("Name:    Marshal Pfluger");
       System.out.println("Course:  COSC 4302 Operating Systems");
       System.out.println("Project: Seven\n\n");
    } // End of the developerInfo method
}
