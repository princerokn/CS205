# 8-Puzzle
8-puzzle is a combination puzzle that challenges a player to slide pieces along certain routes to establish a certain end-configuration (goal-state). The 8-puzzle is a smaller version of 15-puzzle. It consists of 3x3 area with tiles numbered 1 to 8 and one blank space where tiles can be slid to solve the puzzle or attain the goal state. Similarly in 15-puzzle there is 4x4 tile area with 15 numbered tiles and one blank

# Methods
• “main” takes the input choices.
•	“generalSearch” this is where the most suitable puzzle state is chosen and compared to attain goal state. 
•	"printPuzzleState"  prints the current state of the puzzle. 
•	"addToQueue" creates a node and add the puzzle state to the queue. 
•	"comparePuzzle" compares the two puzzles states and return true if they are same otherwise false. 
•	"exploredCheck" checks if the state of the puzzle is already explored.
•	"getChildren" returns a list of children after moving left, right, up or down. 
•	"generateGoalState" generates the goal state of n-puzzle.
