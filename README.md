# GraphPlanning

This is the code implementation of the classical Graph Planning algorithm in Artificial Intelligence. This algorithm is find the most optimal
way to attain your goal in the domain of planning problems for Artificially Intelligent agents. 

The input file is named, pddl.txt which contains a set of initial states and pre-conditions and post-conditions for actions that are possible
for our intelligent system.

The following Java implementation of planning graph has three main features- 
1. To print preconditions and actions at every level until the goal state is found.
2. To identify mutexes between actions and pre-conditions.
3. To identify the solution path solved by DFS after all paths and nodes are constructed.

A mutex is defined as, 'mutual exclusion'; it can exsist between two actions or between two preconditions.

NOTE: Make sure you change the filepath in the code, (GraphPlanAlgo.java) Line 16.
