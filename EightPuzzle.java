import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import static java.lang.System.exit;

public class EightPuzzle {
    static int puzzleDimension = 3; // This is to determine if its a 8-puzzle or 15-puzzle etc.
    static PriorityQueue<Node> queue = new PriorityQueue<>(new PQComparator()); //Priority queue will sort on the minimum heuristic value
    static ArrayList<Node> explored = new ArrayList<>(); // This will store the explored list
    static int returnValue = -1;
    //Sample harcoded input
    static int[][] trivial = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    static int[][] veryEasy = {{1, 2, 3}, {4, 5, 6}, {7, 0, 8}};
    static int[][] easy = {{1, 2, 0}, {4, 5, 3}, {7, 8, 6}};
    static int[][] doable = {{0, 1, 2}, {4, 5, 3}, {7, 8, 6}};
    static int[][] ohBoy = {{8, 7, 1}, {6, 0, 2}, {5, 4, 3}};
    static int[][] goalState;
    private static int nodesExpanded = 0;
    private static int maxQueueSize = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice, difficultyLevel = 0, algorithmChoice;
        long startTime = 0, endTime = 0;
        int[][] customPuzzle;

        System.out.println("***** Welcome to 8-Puzzle Game Solver *****");
        System.out.println("Enter '1' to use a default puzzle, or '2' to create your own.");
        choice = sc.nextInt(); //select default or custom
        System.out.println("Enter your choice for algorithm:\n" +
                "1) Uniform Cost Search\n" +
                "2) A* with the Misplaced Tile heuristic\n" +
                "3) A* with the Manhattan Distance heuristic.");
        algorithmChoice = sc.nextInt();
        if (algorithmChoice < 1 || algorithmChoice > 3) {
            System.out.println("Enter correct choice 1,2 or 3");
            exit(-1);
        }
        if (choice == 1) {  //default puzzle
            System.out.println("Please difficulty level:\n1) Trivial\n2) Very Easy\n3) Easy\n4) Doable\n5) Oh Boy");
            difficultyLevel = sc.nextInt();
            if (difficultyLevel > 5 || difficultyLevel < 1) {
                System.out.println("Enter correct choice:\n1) Trivial\n2) Very Easy\n3) Easy\n4) Doable\n5) Oh Boy");
                exit(-1);
            }
            generateGoalState();
            startTime = System.currentTimeMillis();
            switch (difficultyLevel) {
                case 1:
                    System.out.println("Input state:");
                    printPuzzleState(trivial);
                    returnValue = generalSearch(trivial, algorithmChoice);
                    break;
                case 2:
                    System.out.println("Input state:");
                    printPuzzleState(veryEasy);
                    returnValue = generalSearch(veryEasy, algorithmChoice);
                    break;
                case 3:
                    System.out.println("Input state:");
                    printPuzzleState(easy);
                    returnValue = generalSearch(easy, algorithmChoice);
                    break;
                case 4:
                    System.out.println("Input state:");
                    printPuzzleState(doable);
                    returnValue = generalSearch(doable, algorithmChoice);
                    break;
                case 5:
                    System.out.println("Input state:");
                    printPuzzleState(ohBoy);
                    returnValue = generalSearch(ohBoy, algorithmChoice);
                    break;
            }
            endTime = System.currentTimeMillis();
        } else if (choice == 2) { //custom puzzle
            System.out.println("Enter the dimension of you puzzle eg: 3 for 8-puzzle,  4 for 15-puzzle etc.");
            puzzleDimension = sc.nextInt(); //Enter the dimension of your custom puzzle
            customPuzzle = new int[puzzleDimension][puzzleDimension];
            System.out.println("Enter your custom puzzle with white space between the numbers and hit enter for next row");
            sc.nextLine();
            for (int i = 0; i < puzzleDimension; i++) {
                String[] row;
                String line = sc.nextLine();
                if (line == null) {
                    System.err.println("Invalid Entry");
                    exit(1);
                }
                row = line.split(" ");
                for (int j = 0; j < puzzleDimension; j++)
                    customPuzzle[i][j] = Integer.parseInt(row[j]);
            }
            System.out.println("Input state:");
            printPuzzleState(customPuzzle);
            generateGoalState();
            startTime = System.currentTimeMillis();
            returnValue = generalSearch(customPuzzle, algorithmChoice);
            endTime = System.currentTimeMillis();
        } else {
            System.out.println("Enter correct choice 1 or 2");
            exit(-1);
        }

        if (returnValue == 0) {
            System.out.println("The Puzzle is Solved!");
            System.out.println("Number of nodes expanded: " + nodesExpanded);
            System.out.println("Max queue size: " + maxQueueSize);
            System.out.println("Time taken: " + (endTime - startTime) + "ms");
        } else {
            System.out.println("Error: Given input has no solution!");
        }
    }

    //This is where the most suitable puzzle state is chosen and compared to attain goalState
    static int generalSearch(int[][] currPuzzle, int algorithm) {
        addToQueue(currPuzzle, 0, algorithm); //initial state
        while (true) {
            if (queue.isEmpty())
                return -1;
            ArrayList<int[][]> children;
            Node tempState = queue.poll();
            int[][] tempNode = tempState.getPuzzle();
            int gN = tempState.getgN();
            int hN = tempState.gethN();
            System.out.println("Fetching the node from queue g(n)=" + gN + " and h(n)=" + hN);
            printPuzzleState(tempState.getPuzzle());
            if (comparePuzzle(tempState.getPuzzle(), goalState)) { //Goal
                return 0;
            } else {  // Goal state not found, add eligible child nodes
                children = getChildren(tempState);
                if (children.size() == 0) {
                    continue;  //No eligible child
                }
                nodesExpanded++;
                for (int[][] child : children) {
                    if (!exploredCheck(child)) {  // check if already explored
                        addToQueue(child, gN + 1, algorithm);
                        if (queue.size() > maxQueueSize)
                            maxQueueSize = queue.size();
                    }
                }
            }
        }
    }

    //This will print the current state of the puzzle
    static void printPuzzleState(int[][] puzzle) {
        for (int i = 0; i < puzzleDimension; i++) {
            for (int j = 0; j < puzzleDimension; j++) {
                System.out.print(puzzle[i][j] + " ");
            }
            System.out.println();
        }
    }
    //This will create a node add the puzzle state to the queue
    static void addToQueue(int[][] puzzle, int gN, int algorithm) {
        int hN = 0;
        switch (algorithm) {
            case 1: //Uniform Cost
                hN = 0;
                break;
            case 2: //A* misplaced tile
                for (int i = 0; i < puzzleDimension; i++)
                    for (int j = 0; j < puzzleDimension; j++) {
                        if (puzzle[i][j] != goalState[i][j])
                            hN++;
                    }
                break;
            case 3: //A* manhattan distance
                for(int i = 0; i < puzzleDimension; i++)
                    for(int j = 0; j < puzzleDimension; j++) {
                        int target = puzzle[i][j];
                        for(int k = 0; k < puzzleDimension; k++)
                            for (int l = 0; l < puzzleDimension; l++) {
                                if (goalState[k][l] == target) {
                                    hN += Math.abs(k - i) + Math.abs(l - j);
                                }
                            }
                    }
                break;
            default:
                System.err.println("Wrong algorithm choice");
                break;
        }
        Node currNode = new Node(puzzle, gN, hN);
        queue.add(currNode);
        explored.add(currNode);
    }

    //This will compare the two puzzles states and return true if they are same otherwise false
    static boolean comparePuzzle(int[][] puzzle1, int[][] puzzle2) {
        for (int i = 0; i < puzzleDimension; i++)
            for (int j = 0; j < puzzleDimension; j++)
                if (puzzle1[i][j] != puzzle2[i][j])
                    return false;
        return true;
    }

    //checks if the state of the puzzle is already explored 
    static boolean exploredCheck(int[][] child) {
        boolean identical = false;
        for (Node n : explored) {
            int[][] temp = n.getPuzzle();
            identical = comparePuzzle(child, temp);
            if (identical)
                return identical;
        }
        return identical;
    }


    //returns a list children after moving left, right, up or down
    static ArrayList<int[][]> getChildren(Node currState) {
        ArrayList<int[][]> list = new ArrayList<>(); //this will store list of eligble child 
        int[][] currPuzzle = currState.getPuzzle();
        int blankX = -1,blankY = -1; //coordinates of blank or zero
        for (int i = 0; i < puzzleDimension; i++) {
            for (int j = 0; j < puzzleDimension; j++) {
                if (currPuzzle[i][j] == 0) {
                    blankX = i;
                    blankY = j;
                }
            }
        }
        if (blankX - 1 >= 0) {  //move up
            int[][] tempState = new int[puzzleDimension][];
            for (int i = 0; i < puzzleDimension; i++)
                tempState[i] = currPuzzle[i].clone();
            int temp = tempState[blankX][blankY];
            tempState[blankX][blankY] = tempState[blankX - 1][blankY];
            tempState[blankX - 1][blankY] = temp;
            list.add(tempState);
        }
        if (blankX + 1 < puzzleDimension) {  //move down
            int[][] tempState = new int[puzzleDimension][];
            for (int i = 0; i < puzzleDimension; i++)
                tempState[i] = currPuzzle[i].clone();

            int temp = tempState[blankX][blankY];
            tempState[blankX][blankY] = tempState[blankX + 1][blankY];
            tempState[blankX + 1][blankY] = temp;
            list.add(tempState);
        }
        if (blankY - 1 >= 0) {  //move left
            int[][] tempState = new int[puzzleDimension][];
            for (int i = 0; i < puzzleDimension; i++)
                tempState[i] = currPuzzle[i].clone();

            int temp = tempState[blankX][blankY];
            tempState[blankX][blankY] = tempState[blankX][blankY - 1];
            tempState[blankX][blankY - 1] = temp;
            list.add(tempState);
        }
        if (blankY + 1 < puzzleDimension) {  //move right
            int[][] tempState = new int[puzzleDimension][];
            for (int i = 0; i < puzzleDimension; i++)
                tempState[i] = currPuzzle[i].clone();
            int temp = tempState[blankX][blankY];
            tempState[blankX][blankY] = tempState[blankX][blankY + 1];
            tempState[blankX][blankY + 1] = temp;
            list.add(tempState);
        }
        return list;
    }
    //This will generate the goal state of n-puzzle
    static void generateGoalState() {
        goalState = new int[puzzleDimension][puzzleDimension];
        int counter = 1;
        for (int i = 0; i < puzzleDimension; i++) {
            for (int j = 0; j < puzzleDimension; j++) {
                goalState[i][j] = counter++;
            }
        }
        goalState[puzzleDimension - 1][puzzleDimension - 1] = 0;
    }
}

//This data structure represent the puzzle and its state
class Node {
    private final int[][] puzzle; //puzzle board
    private final int gN; // cost so far to reach goal state
    private final int hN; // heuristic cost or estimated cost to reach goal state

    //constructor and getters
    public Node(int[][] puzzle, int gN, int hN) {
        this.puzzle = puzzle;
        this.gN = gN;
        this.hN = hN;
    }

    public int[][] getPuzzle() {
        return puzzle;
    }

    public int getgN() {
        return gN;
    }

    public int gethN() {
        return hN;
    }
}
//Comparator class to sort the nodes
class PQComparator implements Comparator<Node> {
    public int compare(Node n1, Node n2) {
        int Hn1 = n1.gethN();
        int Hn2 = n2.gethN();
        if ((Hn1 - Hn2) == 0)
            return n1.getgN() - n2.getgN();
        return Hn1 - Hn2;
    }
}

