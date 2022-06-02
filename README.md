# Book Finding AI

# Description

According to the bestseller of Joanne Rowling called Harry Potter and Philosopher’s Stone, the main hero was looking for a book about Nicholas Flamel, the only known creator of a philosopher’s stone. However, the book was located in the restricted section of a library. Therefore, Harry has decided to use his invisibility cloak to hide from unexpected guests and go to the library during the night. However, he has lost his invisibility cloak somewhere in the library while looking for a book. At that time, Argus Filch and his cat Mrs Norris began inspecting the library. Harry has to find the book and leave the library without being caught.

# How to start

- If you are using **IDE**, e.g. *Intellij IDEA*, then just open the main file and launch it using IDE tool.
- You can launch the code using console:

```bash
java AndreyKuzmickiy.java
```

**Note:** you should be at the same path with file `AndreyKuzmickiy.java`.

# Input

After you start the program, you should provide positions for all actors on the field, as well as, scenario for Harry’s perception zone.

### **Format** for input:

```bash
[0,0] [4,2] [2,7] [7,4] [0,8] [1,4]
1
```

Describes the positions of all actors in the first figure:

- `[0,0]` - Harry
- `[4,2]` - Argus Filch
- `[2,7]` - Mrs. Norris
- `[7,4]` - Book
- `[0,8]` - Invisibility Cloak
- `[1,4]` - Exit

You can leave position description blank to set them randomly (except, Harry: he will always start from `[0,0]` - bottom left corner).

### **Restrictions:**

- You should provide positions for all actors (6 actors), or for no one (to set them randomly).
- All positions should be **non-negative** and **less** than `9`.
- No actor must have the same coordinates as the other (in other words, **each cell contains only 1 actor**). Because it does not make sense to put 2 objects at the same place (Argus cannot stay on a cat (Mrs. Norris), Book cannot be under Invisibility Cloak (we couldn’t find it), and so on, it creates ambiguity)
- Exit, Book, Invisibility Cloak cannot be in the inspectors’ zone (Harry can be, but the game will eventually be lost).

# Output

**Note:** If user entered incorrect data, program prints corresponding message with error description to the console.

First of all, the program prints game field to the console. Second, launch **Backtracking** algorithm and prints it’s output. Finally, launch the second algorithm (**A star**) and prints it’s output.

**Note:** User could enter coordinates for an inspector too close to Harry, that Harry stays in inspector’s perception zone. In this case, the game accept input data and prints the message: `Loss due to wrong field`, after that, it stops execution.

### Output for algorithms

- Algorithm’s name
- If algorithm could not find the path to book or to exit (taking the book), it will print: `Lose!`. After loss, the algorithm has nothing to print.
- If algorithm found the path, then it prints: `Win!`.
    - Number of steps is an amount of Harry’s steps to reach the exit taking the book.
    - Path is a list of cell coordinates where Harry stepped on to reach the book and exit, including starting position of the Harry.
    - Map with the steps taken by Harry: symbol `$` means that Harry was there.
    - Time taken by the algorithm.

### Designations on a map

`H` - Harry

`F` - Argus Filch

`C` - Mrs. Norris, cat of Argus

`B` - Book

`X` - Exit

`P` - Invisibility Cloak

`.` - Empty cell

`*` - Inspectors’ perception zone

`$` - Harry’s path

# Algorithm description

Algorithms uses the following priority for cells to check availability to step on them. Cell with minimum priority will be checked first (clockwise).

The distance to each cell (including diagonal) is $1$. Therefore, due to priority and the distance for movement, the best path may be displayed oddly.

![algorithm_description.jpeg](Book%20finding%204365f1fe66ee4e24973da49371520d93/6B1DD539-F86A-4EBC-AE54-738A0B9D9C9B.jpeg)

## Backtracking (improved)

Harry walks on the map and saves the shortest distance to each cell (which he visited) with it’s parent (previous cell from which to step on). It’s needed to reduce the number of algorithm operations.

When Harry found a book, he starts backtrack on 2 steps before and continue to look for the best path to a book. In the worst case, Harry won’t find the book and the cloak and continue to update map with minimal path to each cell.

This algorithm looks for the best (shortest) path, that’s why Harry looks for the map several times. The first time he just analyze the map and find at least 1 path (but it might not be the best path). Therefore, algorithm tries to find the best path from these 3 possible scenarios:

- Book → Exit
- Book → Cloak → Exit
- Cloak → Book → Exit

This algorithm finds the best path on the $1^{st}$ scenario, but on the $2^{nd}$ scenario, it might not find the best path (usually, on 1 step more than the minimal possible path) because he looks for the safest move using `blindStep()` method.

**Maximum working time:** **`50 ms`**

## A star (improved)

Works like usual A star algorithm with priority queue (*OPEN*) and lists *CLOSED* and *BLOCKED*, but it is improved by `blindStep()`. The algorithm works the same in different scenarios.

First it checks the current position of Harry. Then it checks Harry’s perception zone. Finally, it checks cells around Harry, if the cells are unknown and the next element from *OPEN* is also unknown, then algorithm calls `blindStep()` on the current cell.

It uses map to save the shortest path to book and to exit. Each cell on the map contains distance (*gCost*), heuristics (*hCost*) and previous cell from which to step on (*parent*). At the beginning, the algorithm works like **Dijkstra algorithm** because Harry does not know where is a book that’s why heuristics on each cell is zero. After the book was found, the algorithm saves the path to the book and updates the cells on map (nullify the distance with parent and set heuristics to the exit) and works like usual A* algorithm.

**Maximum working time: `10 ms`**

## blindStep method

Algorithms use this method when they cannot decide where to move. Main idea of the blind step is to set the priority for each cell around Harry. The cell with minimal priority will be the next move.

Priority is a number of perception cell of the inspector which Harry sees.

How to calculate priority for:

- Straight cells: sum up inspector perception cells only on 3 visible cells of a direction that straight cell.
*Maximum priority* for straight cell: `**3**`
- Diagonal cells: sum up inspector perception cells on adjacent visible zones. 2 cells for each adjacent visible zones.
*Maximum priority* for diagonal cell: **`4`**

If there are several cells with equal priority, then Harry chooses the first cell on clockwise position. For example, on a picture Harry chose the bottom cell.

![movement_directions.jpeg](Book%20finding%204365f1fe66ee4e24973da49371520d93/BB91DE46-3552-423C-A77E-3FEF8E90D409.jpeg)

# Statistics

I tested 250 games ($125$ for the $1^{st}$ scenario, $125$ for the $2^{nd}$) using random generator of the map (leaving agent positions empty).

- $7\%$ loss due to wrong field. Inspectors were spawned too close to Harry. For the comparison of algorithms, I removed that data from the calculations of statistics because it does not affect on the algorithm comparison.

### Backtracking (scenario 1) vs. A star (scenario 1)

|  | Win | Lose | Steps min | Steps max | Steps median | Time min | Time max | Time mean |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Backtracking | 106 | 0 | 3 | 22 | 10 | 3 ms | 18 ms | 7.906 ms |
| A star | 106 | 0 | 3 | 28 | 10 | 1 ms | 7 ms | 2.971 ms |
- Backtracking and A star work successfully (their win rate is $100\%$).
- Backtracking found better paths than A star in some cases, however medians are equal, this means that both algorithms find equal paths on average.
- A star works much faster than Backtracking because Backtracking should look for the better path from 3 possible variants.

### Backtracking (scenario 2) vs. A star (scenario 2)

|  | Win | Lose | Steps min | Steps max | Steps median | Time min | Time max | Time mean |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Backtracking | 108 | 5 | 5 | 24 | 11 | 7 ms | 29 ms | 16.213 ms |
| A star | 111 | 2 | 4 | 24 | 10 | 1 ms | 8 ms | 3.306 ms |
- Backtracking has less win rate than A star because A star uses priority queue and it uses blind step only when no known cells in it’s priority queue.
- A star can find better path due to priority queue usage. It allows A star to look for the path on known cells, and Backtracking usually moves to unknown cells.
- Time taken by algorithms is also different due to 3 possible variants of Backtracking algorithm.

### Backtracking (scenario 1) vs. Backtracking (scenario 2)

|  | Win | Lose | Steps min | Steps max | Steps median | Time min | Time max | Time mean |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Scenario 1 | 106 | 0 | 3 | 22 | 10 | 3 ms | 18 ms | 7.906 ms |
| Scenario 2 | 108 | 5 | 5 | 24 | 11 | 7 ms | 29 ms | 16.213 ms |
- The $2^{nd}$ scenario allows blind step on inspector perception zone, therefore Backtracking on $2^{nd}$scenario might lose sometimes.
- Due to far perception zone of Harry, he step only on safe cells. Therefore, the minimal path to the exit taken the book is greater than for the $1^{st}$ scenario.
- Due to big amount of calls `blindStep` on the $2^{nd}$ scenario, time to find better path is much bigger than for the $1^{st}$ scenario.

### A star (scenario 1) vs. A star (scenario 2)

|  | Win | Lose | Steps min | Steps max | Steps median | Time min | Time max | Time mean |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Scenario 1 | 106 | 0 | 3 | 28 | 10 | 1 ms | 7 ms | 2.971 ms |
| Scenario 2 | 111 | 2 | 4 | 24 | 10 | 1 ms | 8 ms | 3.306 ms |
- Due to far perception zone of Harry, he can step on bad cell and lose the game, but in the $1^{st}$ scenario algorithm works perfectly.
- The first step of Harry is a call `blindStep`, therefore the starting part of the path is set and might not be changed due to priority queue. Thus, the minimal number of steps can be different on 1 cell.
- Time is the same for both algorithms. Difference of 1 ms is due to calling `blindStep` method.
