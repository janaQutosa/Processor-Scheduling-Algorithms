import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Process {
	
    int processId;
    int burstTime;
    int arrivalTime;
    int returnTime;
    int originalBurstTime;
    int priority;
	private boolean completed;
    boolean inQueue;
    int remainingWaitingTime;
	public boolean in_queue;
    int timeInQueue;
    List<int[]> startEndPair;



    public Process(int processId, int burstTime, int arrivalTime, int blockTime,int priority) {
        this.processId = processId;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.returnTime = blockTime;
        this.priority = priority;
        this.originalBurstTime= burstTime;
        this.completed=false;
        this.in_queue = false;
        this.remainingWaitingTime = 0;
        this.timeInQueue=0;
        this.startEndPair = new ArrayList<>();


    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    } 
    public void addStartEndPair(int start, int duration) {
        this.startEndPair.add(new int[]{start, duration});
    }

}

public class os {
    public static void main(String[] args) {
    	
    	FCFS();
    	SJF();
    	SRTF();
    	RR();
    	PreemptivePriorityScheduling();
    	nonPreemptivePriorityScheduling();
   		
    }
    
    private static Process[] initializeProcesses() {
        Process[] processes = new Process[7];
        processes[0] = new Process(1, 10, 0, 2, 3);
        processes[1] = new Process(2, 8, 1, 4, 2);
        processes[2] = new Process(3, 14, 3, 6, 3);
        processes[3] = new Process(4, 7, 4, 8, 1);
        processes[4] = new Process(5, 5, 6, 3, 0);
        processes[5] = new Process(6, 4, 7, 6, 1);
        processes[6] = new Process(7, 6, 8, 9, 2);
        return processes;
    } 
    
    private static void FCFS() {
    	System.out.println("\n-------------------FCFS Scheduling Algorithm---------------------");
        System.out.println("\nGantt chart");

        Process[] processes = initializeProcesses();
       
        int currentTime = 0, k = 0, z = 0, waiting = 0, turnaround = 0, t = 0;
        
        int[] saveArrival = {0, 1, 3, 4, 6, 7, 8};
        int[][] save = new int[200][2];

        while (currentTime <= 200) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    if ((processes[i].arrivalTime < processes[j].arrivalTime) |
                            ((processes[i].processId < processes[j].processId) && (processes[i].arrivalTime == processes[j].arrivalTime))) {
                        Process temp = processes[i];
                        processes[i] = processes[j];
                        processes[j] = temp;
                    }
                }
            }

            // Simulation of FCFS disk scheduling algorithm
            if ((currentTime + processes[0].burstTime) <= 200) {
                currentTime += processes[0].burstTime;
                k += processes[0].burstTime;
                processes[0].arrivalTime = currentTime + processes[0].returnTime;
                
            } else 
               break;
                     
            System.out.println("P" + processes[0].processId + " --> " + k);
            save[z][0] = processes[0].processId;
            save[z][1] = k;
            z++;
        }
                
        int timeSpentInCPU=0, Total_waiting=0,Total_turnaround=0;
            
        for(int i=0;  i<7;  i++)
        {
          for(int j=0;  j<z;  j++)
            {
              if (save[j][0]==processes[i].processId)   
              {
                   if (j==0)
                   {
                	   timeSpentInCPU += save[j][1];
                       t=save[j][1];
                   }
                   else
                   {
                	   timeSpentInCPU += save[j][1]-save[j-1][1]; 
                     t=save[j][1];
                   }
                }
            }

         waiting = t - saveArrival[i] -timeSpentInCPU;
         turnaround = waiting+timeSpentInCPU;
         
           Total_waiting += waiting; 
           Total_turnaround += turnaround; 
           timeSpentInCPU=0; 
        }

        System.out.println("\n Avg waiting time: " + (Total_waiting/7.0));
        System.out.println("Avg turn around time: " + (Total_turnaround/7.0));  


    }
   
    private static void SJF() {
        System.out.println("\n-----------------------SJF Scheduling Algorithm--------------------------");
        System.out.println("\nGantt chart ");

        Process[] processes = initializeProcesses();

        int numOfProcesses = 7, totalWaiting = 0, totalTurnaround = 0;
        int currentTime = 0, totalExecutionTime = 0, ganttIndex = 0, waiting = 0, turnaround = 0;
        int[] saveExecutedProcesses = new int[7];

        int[] saveArrival = {0, 1, 3, 4, 6, 7, 8};
        int[][] save = new int[200][2];

        Arrays.sort(processes, Comparator.comparingInt(p -> p.burstTime));

        while (currentTime <= 200) {
            Process selectedProcess = null;

            for (Process process : processes) {
                if (process.arrivalTime <= currentTime) {
                    if (selectedProcess == null || process.burstTime < selectedProcess.burstTime) {
                        selectedProcess = process;
                    }
                }
            }

            if (selectedProcess == null || currentTime + selectedProcess.burstTime > 200) {
                break;
            }

            currentTime += selectedProcess.burstTime;
            totalExecutionTime += selectedProcess.burstTime;
            selectedProcess.arrivalTime = currentTime + selectedProcess.returnTime;

            System.out.println("P" + selectedProcess.processId + " --> " + totalExecutionTime);
            save[ganttIndex][0] = selectedProcess.processId;
            save[ganttIndex][1] = totalExecutionTime;
            ganttIndex++;

            for (int j = 0; j < 7; j++) {
                if (saveExecutedProcesses[j] == 0) {
                    saveExecutedProcesses[j] = selectedProcess.processId;
                    break;
                }
            }
        }


        for (int i = 0; i < ganttIndex; i++) {
            int processId = save[i][0];

            Process process = null;
            for (Process p : processes) {
                if (p.processId == processId) {
                    process = p;
                    break;
                }
            }

            waiting = save[i][1] - saveArrival[process.processId - 1];
            totalWaiting += waiting;

            turnaround = waiting + process.burstTime;
            totalTurnaround += turnaround;
        }
        
        for (int j = 0; j < 7; j++) {
            boolean found = false;
            for (int k = 0; k < ganttIndex; k++) {
                if (saveExecutedProcesses[j] == save[k][0]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("\n Infinite waiting time");
                return;  
            }
        }

       // System.out.println("\nAverage Waiting Time: " + (totalWaiting / (double) numOfProcesses));
        System.out.println("\nAverage Turnaround Time: " + (totalTurnaround / (double) numOfProcesses));
        System.out.println("infinite waiting time ");

    }

 
    private static void SRTF() {
        System.out.println("\n---------------------- SRTF Scheduling Algorithm-------------------------");
        System.out.println("\nGantt chart");

        Process[] processes = initializeProcesses();

        int numOfProcesses = 7, totalWaiting = 0, totalTurnaround = 0;
        int currentTime = 0, k = 0, z = 0, waiting = 0, turnaround = 0, t = 0;

        int[][] save = new int[200][2];
        System.out.println("    time 0");

        while (currentTime <= 200) {
            Process selectedProcess = null;

            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && process.burstTime > 0) {
                    if (selectedProcess == null || process.burstTime < selectedProcess.burstTime ||
                            (process.burstTime == selectedProcess.burstTime && process.processId < selectedProcess.processId)) {
                        selectedProcess = process;
                    }
                }
            }

            if (selectedProcess == null) {
                currentTime++;
                continue;
            }
            currentTime++;
            k++;
            selectedProcess.burstTime--;

            if (selectedProcess.burstTime == 0) {
                if (z == 0 || save[z - 1][0] != selectedProcess.processId) {
                    System.out.println("P" + selectedProcess.processId + " --> " + k);
                }

                // Calculate waiting time and turnaround time
                waiting = k - selectedProcess.originalBurstTime - selectedProcess.arrivalTime;
                turnaround = k - selectedProcess.arrivalTime;

                // Update total waiting and turnaround times
                totalWaiting += waiting;
                totalTurnaround += turnaround;

                // Save the completion time for the process
                save[z][0] = selectedProcess.processId;
                save[z][1] = k;
                z++;

                // Reset process parameters
                selectedProcess.arrivalTime = currentTime + selectedProcess.returnTime;
                selectedProcess.burstTime = selectedProcess.originalBurstTime;
            }
            
            
        }

        // Calculate average waiting and turnaround times
        double avgWaitingTime = totalWaiting / (double) numOfProcesses;
        double avgTurnaroundTime = totalTurnaround / (double) numOfProcesses;

       // System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("\nAverage Turnaround Time: " + avgTurnaroundTime);
        System.out.println("infinite waiting time ");

    }

    private static void RR() {
        System.out.println("\n------------------------Round Robin Scheduling Algorithm with q = 5----------------------------");

        Process[] processes = initializeProcesses();
        List<int[]> startEndPairs = new ArrayList<>();

        int currentTime = 0;
        final int current = currentTime;

        List<Process> readyQueue = new ArrayList<>(); 
        List<Process> waitingQueue = new ArrayList<>();
        int timeQuantum = 5;

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        while (currentTime < 200) {
            for (Process process : processes) {
                if (process.arrivalTime <= currentTime && !process.inQueue) {
                    readyQueue.add(process);
                    process.inQueue = true;
                }
            }

            Collections.sort(readyQueue, Comparator.comparingInt(p -> p.arrivalTime));

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.get(0);
                readyQueue.remove(currentProcess);

                int startTime = Math.max(currentTime, currentProcess.arrivalTime);
                int endTime = Math.min(startTime + timeQuantum, startTime + currentProcess.burstTime);
                currentProcess.burstTime -= (endTime - startTime);
                int[] pair = {currentProcess.processId, startTime, endTime};
                startEndPairs.add(pair);
                currentTime = endTime;

                if (currentProcess.burstTime == 0) {
                    int waitingTime = startTime - currentProcess.arrivalTime;
                    totalWaitingTime += waitingTime;

                    int turnaroundTime = startTime + currentProcess.originalBurstTime - currentProcess.arrivalTime;
                    totalTurnaroundTime += turnaroundTime;

                    currentProcess.arrivalTime = currentTime + currentProcess.returnTime;
                    currentProcess.burstTime = currentProcess.originalBurstTime;
                    waitingQueue.add(currentProcess);
                } else {
                    readyQueue.add(currentProcess);
                    currentProcess.arrivalTime = currentTime;
                }
            }

            waitingQueue.removeIf(process -> current >= process.arrivalTime);
            readyQueue.addAll(waitingQueue);
        }

        System.out.println("\n Gantt Chart:");
        for (int[] pair : startEndPairs) {
            System.out.println("P" + pair[0] + " --> " + pair[2]);
        }

        // Calculate average waiting and turnaround times
        double avgWaitingTime = totalWaitingTime / (double) processes.length;
        double avgTurnaroundTime = totalTurnaroundTime / (double) processes.length;

        // Print results
        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }

    
        public static void PreemptivePriorityScheduling() {
        	  System.out.println("\n----------------------preemptive Priority Scheduling Algorithm---------------------------");
              System.out.println("\nGantt chart ");

              Process[] processes = initializeProcesses();

              int num_of_processes = 7, temp = 0;
              int time = 0, iterationCounter = 0, time_spent_in_cpu = 0, waiting = 0, timeIndex = 0, turnaround = 0, processIndex = 0, t = 0,
                      Total_waiting = 0, Total_turnaround = 0, x = 0, y = 0, remain = 0;
              int[] saveArrival = {0, 1, 3, 4, 6, 7, 8};
              int[] saveReturn = {2, 4, 6, 8, 3, 6, 9};
              int[] arr = {0, 1, 3, 4, 6, 7, 8};
              int[] save_pr = {3, 2, 3, 1, 0, 1, 2};
              int save[][] = new int[200][2];

              while (time <= 200) {
                  for (int i = 0; i < num_of_processes; i++) {
                      for (int j = 0; j < num_of_processes; j++) {
                          if ((processes[i].priority < processes[j].priority) || ((processes[i].priority == processes[j].priority) && (processes[i].arrivalTime < processes[j].arrivalTime))) {
                              temp = processes[i].arrivalTime;
                              processes[i].arrivalTime = processes[j].arrivalTime;
                              processes[j].arrivalTime = temp;

                              temp = processes[i].processId;
                              processes[i].processId = processes[j].processId;
                              processes[j].processId = temp;

                              temp = processes[i].burstTime;
                              processes[i].burstTime = processes[j].burstTime;
                              processes[j].burstTime = temp;

                              temp = processes[i].returnTime;
                              processes[i].returnTime = processes[j].returnTime;
                              processes[j].returnTime = temp;

                              temp = processes[i].priority;
                              processes[i].priority = processes[j].priority;
                              processes[j].priority = temp;

                              temp = save_pr[i];
                              save_pr[i] = save_pr[j];
                              save_pr[j] = temp;

                              temp = arr[i];
                              arr[i] = arr[j];
                              arr[j] = temp;
                          }
                      }
                  }

                  for (int i = 0; i < num_of_processes; i++) {
                      if (processes[i].arrivalTime <= time) {
                          processIndex = i;
                          break;
                      }
                  }

                  if ((time + processes[processIndex].burstTime) <= 200) {
                      time += processes[processIndex].burstTime;
                      iterationCounter = time;
                      processes[processIndex].arrivalTime = time + processes[processIndex].returnTime;
                      arr[processIndex] = time + processes[processIndex].returnTime;
                      processes[processIndex].priority = save_pr[processIndex];

                      for (int h = 0; h < num_of_processes; h++) {
                          if ((processes[h].arrivalTime <= time) && (h != processIndex)) {
                              if ((time - arr[h]) >= 5) {
                                  if ((processes[h].priority - 1) < 0)
                                      processes[h].priority = 0;
                                  else {
                                      processes[h].priority--;
                                      arr[h] = arr[h] + 5;
                                  }
                              }
                          }
                      }
                  } else {
                      for (int l = 1; l <= processes[processIndex].burstTime; l++) {
                          time++;
                          if (time > 200) {
                              iterationCounter = time - 1;
                              break;
                          }
                      }
                  }

                  System.out.println("P" + processes[processIndex].processId + "  -->  " + iterationCounter);
                  save[timeIndex][0] = processes[processIndex].processId;
                  save[timeIndex][1] = iterationCounter;
                  timeIndex++;
              }


              for (int i = 0; i < num_of_processes; i++) {
                  for (int j = 0; j < timeIndex; j++) {
                      if (save[j][0] == processes[i].processId) {
                          if (j == 0) {
                              time_spent_in_cpu += save[j][1];
                              t = save[j][1];
                          } else {
                              time_spent_in_cpu += save[j][1] - save[j - 1][1];
                              t = save[j][1];
                          }
                      }
                  }

                  if ((time_spent_in_cpu % processes[i].originalBurstTime) == 0) {
                      x = (time_spent_in_cpu / processes[i].originalBurstTime) - 1;
                      y = (time_spent_in_cpu / processes[i].originalBurstTime) - 1;
                  } else {
                      x = (time_spent_in_cpu / processes[i].originalBurstTime);
                      y = (time_spent_in_cpu / processes[i].originalBurstTime);
                  }

                  waiting = t - saveArrival[i] - time_spent_in_cpu - (x * saveReturn[i]);
                  turnaround = waiting + time_spent_in_cpu + (y * saveReturn[i]);
                  Total_waiting = Total_waiting + waiting;
                  Total_turnaround = Total_turnaround + turnaround;
                  time_spent_in_cpu = 0;
                  t = 0;
                  y = 0;
                  x = 0;
                  remain = 0;
              }

              System.out.println("\nAverage waiting time: " + (Total_waiting / 7.0));
              System.out.println("Average turn around time: " + (Total_turnaround / 7.0));
          }
        

    private static void nonPreemptivePriorityScheduling() {

        System.out.println("\n---------------------Non-preemptive Priority Scheduling Algorithm--------------------------");
        System.out.println("\nGantt chart ");

        Process[] processes = initializeProcesses();

        int num_of_processes = 7, temp = 0;
        int time = 0, iterationCounter = 0, time_spent_in_cpu = 0, waiting = 0, timeIndex = 0, turnaround = 0, processIndex = 0, t = 0,
                Total_waiting = 0, Total_turnaround = 0, x = 0, y = 0, remain = 0;
        int[] saveArrival = {0, 1, 3, 4, 6, 7, 8};
        int[] saveReturn = {2, 4, 6, 8, 3, 6, 9};
        int[] arr = {0, 1, 3, 4, 6, 7, 8};
        int[] save_pr = {3, 2, 3, 1, 0, 1, 2};
        int save[][] = new int[200][2];

        
        while (time <= 200) {

            for (int i = 0; i < num_of_processes; i++) {
                for (int j = 0; j < num_of_processes; j++) {

                    if ((processes[i].priority < processes[j].priority) | ((processes[i].priority == processes[j].priority) && (processes[i].arrivalTime < processes[j].arrivalTime))) {
                        temp = processes[i].arrivalTime;
                        processes[i].arrivalTime = processes[j].arrivalTime;
                        processes[j].arrivalTime = temp;

                        temp = processes[i].processId;
                        processes[i].processId = processes[j].processId;
                        processes[j].processId = temp;

                        temp = processes[i].burstTime;
                        processes[i].burstTime = processes[j].burstTime;
                        processes[j].burstTime = temp;

                        temp = processes[i].returnTime;
                        processes[i].returnTime = processes[j].returnTime;
                        processes[j].returnTime = temp;

                        temp = processes[i].priority;
                        processes[i].priority = processes[j].priority;
                        processes[j].priority = temp;

                        temp = save_pr[i];
                        save_pr[i] = save_pr[j];
                        save_pr[j] = temp;

                        temp = arr[i];
                        arr[i] = arr[j];
                        arr[j] = temp;
                    }
                }
            }

            for (int i = 0; i < num_of_processes; i++) {
                if (processes[i].arrivalTime <= time) {
                    processIndex = i;
                    break;
                }
            }

            if ((time + processes[processIndex].burstTime) <= 200) {
                time += processes[processIndex].burstTime;
                iterationCounter = time;
                processes[processIndex].arrivalTime = time + processes[processIndex].returnTime;
                arr[processIndex] = time + processes[processIndex].returnTime;
                processes[processIndex].priority = save_pr[processIndex];

                for (int h = 0; h < num_of_processes; h++) {
                    if ((processes[h].arrivalTime <= time) && (h != processIndex)) {
                        if ((time - arr[h]) >= 5) {
                            if ((processes[h].priority - 1) < 0)
                                processes[h].priority = 0;
                            else {
                                processes[h].priority--;
                                arr[h] = arr[h] + 5;
                            }
                        }
                    }
                }
            } else {
                for (int l = 1; l <= processes[processIndex].burstTime; l++) {
                    time++;
                    if (time > 200) {
                        iterationCounter = time - 1;
                        break;
                    }
                }
            }
            System.out.println("p" + processes[processIndex].processId + "  -->  " + iterationCounter);
            save[timeIndex][0] = processes[processIndex].processId;
            save[timeIndex][1] = iterationCounter;
            timeIndex++;
        }

        for (int i = 0; i < num_of_processes; i++) {
            for (int j = 0; j < timeIndex; j++) {
                if (save[j][0] == processes[i].processId) {
                    if (j == 0) {
                        time_spent_in_cpu += save[j][1];
                        t = save[j][1];
                    } else {
                        time_spent_in_cpu += save[j][1] - save[j - 1][1];
                        t = save[j][1];
                    }
                }
            }

            if ((time_spent_in_cpu % processes[i].originalBurstTime) == 0) {
                x = (time_spent_in_cpu / processes[i].originalBurstTime) - 1;
                y = (time_spent_in_cpu / processes[i].originalBurstTime) - 1;
            } else {
                x = (time_spent_in_cpu / processes[i].originalBurstTime);
                y = (time_spent_in_cpu / processes[i].originalBurstTime);
            }

            waiting = t - saveArrival[i] - time_spent_in_cpu - (x * saveReturn[i]);
            turnaround = waiting + time_spent_in_cpu + (y * saveReturn[i]);
            Total_waiting = Total_waiting + waiting;
            Total_turnaround = Total_turnaround + turnaround;
            time_spent_in_cpu = 0;
            t = 0;
            y = 0;
            x = 0;
            remain = 0;
        }

        System.out.println("\nAverage waiting time: " + (Total_waiting / 7.0));
        System.out.println("Average turn around time: " + (Total_turnaround / 7.0));
    }

 }
