//********************************************************************
//
//  Author:        Marshal Pfluger
//
//  Program #:     Seven
//
//  File Name:     SouthBound.java
//
//  Course:        COSC 4302 Operating Systems
//
//  Due Date:      11/17/2023
//
//  Instructor:    Prof. Fred Kumi
//
//  Java Version:  17
//
//  Description:   Thread for traffic regulation
//
//********************************************************************
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;


public class TravelerThread implements Runnable {
	// Declare instance variables to hold the locks
    private Semaphore northLock;
    private Semaphore southLock;
    private Semaphore bridgeLock;
    
    // Declare instance variables for direction and number
    int farmerNumber;
    String direction;

    //***************************************************************
    //
    //  Method:       Northbound constructor
    // 
    //  Description:  constructor for Northbound Class
    //
    //  Parameters:   Lock lock
    //
    //  Returns:      N/A
    //
    //**************************************************************
    TravelerThread(String direction, int farmerNumber, Semaphore northLock, Semaphore southLock, Semaphore bridgeLock) {
        this.farmerNumber = farmerNumber;
        this.direction = direction;
        this.northLock = northLock;
        this.southLock = southLock;
        this.bridgeLock = bridgeLock;

    }

    //***************************************************************
    //
    //  Method:       run method override
    // 
    //  Description:  overrides the run method from implemented interface
    //
    //  Parameters:   none
    //
    //  Returns:      N/A
    //
    //**************************************************************
    @Override
    public void run() {
    	
    	// Inform that the farmer wants to get on bridge
        printOutput(this.direction + " farmer " + farmerNumber + " wants to use bridge");
        
        try {
        	// try to acquire the locks needed
            acquireLocks();
            
            // Increment the atomic bridge
            Program7.bridgeCount.incrementAndGet();
            
            // Inform that the farmer is on the bridge
            printOutput(this.direction + " farmer " + farmerNumber + " is on the bridge");
            
            // Sleep for a random time to simulate driving accross bridge
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 4000));
            
            // Inform that the farmer is off of the bridge
            printOutput(this.direction + " farmer " + farmerNumber + " is off the bridge");
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        	
            // Only release held direction semaphore when all farmers 
        	// from same direction are off bridge.
            if (Program7.bridgeCount.decrementAndGet() == 0) {
            	
            	// Release the correct semaphore depending on direction
                if (direction.equals("SouthBound")) {
                    southLock.release();
                } else if (direction.equals("NorthBound")) {
                    northLock.release();
                }
            }
        }
    }


    //***************************************************************
    //
    //  Method:       acquireLocks() 
    // 
    //  Description:  handles the semaphore acquisition for the thread
    //
    //  Parameters:   none
    //
    //  Returns:      N/A
    //
    //**************************************************************
    private void acquireLocks() throws InterruptedException {
    	// Set opposite lock bool to false to enter loop 
        boolean gotOppositeLock = false;

        while (!gotOppositeLock) {
        	
        	// Reset lock indicator for new run
            gotOppositeLock = false;
            // declare and initialize lock bool for mylock
            boolean gotMyLock = false;

            try {
            	// Acquire bridge lock to create critical section
                bridgeLock.acquire();

                // try to acquire locks for each direction
                if (direction.equals("SouthBound")) {
                    gotOppositeLock = northLock.tryAcquire();
                    gotMyLock = southLock.tryAcquire();
                } else if (direction.equals("NorthBound")) {
                    gotOppositeLock = southLock.tryAcquire();
                    gotMyLock = northLock.tryAcquire();
                }

            } finally {
            	releaseUnusedLocks(gotMyLock, gotOppositeLock);
            }
        }
    }

    //***************************************************************
    //
    //  Method:       releaseUnusedLocks() 
    // 
    //  Description:  Releases locks that go unused in acquirelocks method
    //
    //  Parameters:   none
    //
    //  Returns:      N/A
    //
    //**************************************************************
    public void releaseUnusedLocks(boolean gotMyLock, boolean gotOppositeLock) {
    	
    	// Release locks if the opposite direction cannot be taken 
        if (direction.equals("SouthBound")) {
            if (gotMyLock && !gotOppositeLock) {
                southLock.release();
            }
            if (gotOppositeLock) {
                northLock.release();
            }
        } else if (direction.equals("NorthBound")) {
            if (gotMyLock && !gotOppositeLock) {
                northLock.release();
            }

            if (gotOppositeLock) {
                southLock.release();
            }
        }
        
    	// Release the bridgelock critical section
        bridgeLock.release();
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
        System.out.println("\n" +output);
    } //End printOutput
}
