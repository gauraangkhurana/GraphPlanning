//
//  GraphPlanAlgorithm
//
//
//  Created by Gauraang Khurana on 3/24/18.
//
//

import java.io.*;
import java.util.*;

	public class GraphPlanAlgo {
		
		
		//PLEASE CHANGE FILE PATH HERE IF NEEDED...
		final static String filePath = "/Users/Isha/eclipse-workspace/Assign3/src/pddl.txt";
		
		
		
		
	    Map<String,ArrayList<ArrayList<ArrayList>>> output = new HashMap<String,ArrayList<ArrayList<ArrayList>>>();
		static ArrayList<ArrayList<ArrayList>> interferenceMutex = new ArrayList<>();
	    static Map<Integer,Set<String>> literalsFinal = new HashMap<Integer,Set<String>>();
		static ArrayList<ArrayList<ArrayList>> inconsistentSupportMutex = new ArrayList<>();

	    static Map<Integer,Set<String>> actionsFinal = new HashMap<Integer,Set<String>>();
		static ArrayList<ArrayList<ArrayList>> competingNodeMutex = new ArrayList<>();

	    static ArrayList<ArrayList> actionMutexesWithLevels = new ArrayList<>();

	    
		static ArrayList<ArrayList<ArrayList>> negatedLiteralsMutex = new ArrayList<>();
		static ArrayList<ArrayList<ArrayList>> inconsistentEffectsMutex = new ArrayList<>();
		static ArrayList solutionPath = new ArrayList<>();
		
		
		public static boolean checkTerminationCondition(ArrayList<String> goalStates, Set<String> set) {
			// TODO Auto-generated method stub
			if(set.containsAll(goalStates))
				return true;
			return false;
		}

		public static void findActionFromPre(Set<Layer> action, ArrayList<Set<String>> stateLayers, ArrayList<Set<Layer>> allActionss, Set<Layer> actionsPath) {
			// TODO Auto-generated method stub
			
			Set<Layer> auxActions = new HashSet<Layer>();
			for(Layer n:action) {
				if(stateLayers.get(stateLayers.size()-1).containsAll(n.preconditionList)) {
					auxActions.add(n);
				}
			}
			literalsFinal.put(stateLayers.size()-1,stateLayers.get(stateLayers.size()-1));
			noOperation(stateLayers.get(stateLayers.size()-1),auxActions);
			allActionss.add(auxActions);
			findNextStates(allActionss,stateLayers);
			
			negatedLiteralsCheck(stateLayers.get(stateLayers.size()-1));
			inconsistentEffectsCheck(allActionss.get(allActionss.size()-1),stateLayers.get(stateLayers.size()-1));
			interferenceCheck(allActionss.get(allActionss.size()-1));
			competingNeeds(allActionss.get(allActionss.size()-1));

			ArrayList<ArrayList> actionMutex = new ArrayList<>();
			actionMutex.addAll(inconsistentEffectsMutex.get(inconsistentEffectsMutex.size()-1));
			actionMutex.addAll(interferenceMutex.get(interferenceMutex.size()-1));
			actionMutex.addAll(competingNodeMutex.get(competingNodeMutex.size()-1));
			
			actionMutexesWithLevels.add(actionMutex);
			findMutexes(allActionss.get(allActionss.size()-1),stateLayers.get(stateLayers.size()-1),actionMutex);
			
			Set<String> actionTemp = new HashSet<String>();
			for(Layer n:allActionss.get(allActionss.size()-1)) {
				actionTemp.add(n.actionName);
			}
			actionsFinal.put(allActionss.size()-1,actionTemp);
		}

		public static void noOperation(Set<String> set, Set<Layer> auxActions) {
			for(String s : set) {
				
				Layer temp = new Layer();
				temp.effectList.add(s);

				temp.actionName = "No-op_"+s;
				temp.preconditionList.add(s);
				auxActions.add(temp);
			}
		}
		
		
		public static void main(String[] args) throws FileNotFoundException {
			Scanner inFile = new Scanner(new FileInputStream(filePath));
			
			ArrayList<Set<Layer>> allActionss = new ArrayList<>();
			Set<Layer> actions = new HashSet<Layer>();
			Set<Layer> actionsPath = new HashSet<>();
			
			ArrayList<Set<String>> stateLayers = new ArrayList<>(); 
			String line = inFile.nextLine();
			String initLine = line.substring(5, line.length()-2);
			String[] initSeparated = initLine.split(",");
			Set<String> states = new HashSet<String>();
			Set<String> statesForPlan = new HashSet<>();
			
			
			String line2 = inFile.nextLine();
			String goal = line2.substring(5,line2.length()-2);
			ArrayList<String> goalStates = new ArrayList<>();
			ArrayList<String> notGoal = new ArrayList<>();
			for(String j:goal.split(",")) {
				goalStates.add(j);
				if(!j.contains("~"))
				notGoal.add("~"+j);
				else
					notGoal.add(j.substring(1,j.length()));
			}

			states.addAll(Arrays.asList(initSeparated));
			states.addAll(notGoal);
			stateLayers.add(states);
			
			statesForPlan.addAll(Arrays.asList(initSeparated));
			statesForPlan.addAll(notGoal);
			
			System.out.println();
			System.out.println("************************************ INIT STATES ****************************************\n\n\t\t" + statesForPlan);
			String lineNext = inFile.nextLine();
			while(!lineNext.isEmpty()) {
				Layer temp = new Layer();
				temp.actionName = lineNext.substring(7, lineNext.length()-1);
				String precond = inFile.nextLine();
				for(String j:precond.substring(8, precond.length()).split(",")) {
					temp.preconditionList.add(j);
				}
				String effect = inFile.nextLine();
				for(String j:effect.substring(7, effect.length()-1).split(",")) {
					temp.effectList.add(j);
				}
				for(String s:goalStates) {
					if(temp.effectList.contains(s)) {
						actionsPath.add(temp);
					}
				}

				
				actions.add(temp);
				if(inFile.hasNext())
				lineNext = inFile.nextLine();
				else
					break;
			}
			System.out.println("\n\n************************************ GOAL STATES ****************************************\n\n\t\t" + goalStates);

			negatedLiteralsMutex.add(null);
			inconsistentSupportMutex.add(null);
			while(!checkTerminationCondition(goalStates,stateLayers.get(stateLayers.size()-1))) {
				findActionFromPre(actions,stateLayers,allActionss,actionsPath);
			}
			findPath(actionsPath,allActionss,actionMutexesWithLevels,statesForPlan); //store in a bool. when true: print the solutionPath.
			findActionFromPre(actions,stateLayers,allActionss,actionsPath);
			findActionFromPre(actions,stateLayers,allActionss,actionsPath);
			literalsFinal.put(stateLayers.size()-1,stateLayers.get(stateLayers.size()-1));

			System.out.println("\n************************************** LITERALS Categorized by Layer**************************************\n");
			for(Integer i : literalsFinal.keySet()) {
				System.out.println(i+" : "+ literalsFinal.get(i));
			}
			
			System.out.println();
			
			System.out.println("************************************** ACTIONS Categorized by Layer**************************************\n");
			for(Integer i : actionsFinal.keySet()) {
				System.out.println(i+" : "+ actionsFinal.get(i));
			}
			
			System.out.println();
			
			
			
			System.out.println("**************************************  MUTEX LINKS Categorized by Type **************************************");
			
			int i = 0;
			
			System.out.println();
			i = 0;
			System.out.print("Inconsistent Effects mutexes at Level : \n\n");
			for(ArrayList ie:inconsistentEffectsMutex) {
				System.out.print(i+": ");	
				System.out.println(ie);
				i++;
			}
			
			System.out.println();
			i = 0;
			System.out.print("Interference mutexes at Level : \n\n");
			for(ArrayList im:interferenceMutex) {
				System.out.print(i+": ");	
				System.out.println(im);
				i++;
			}
			
			System.out.println();
			i = 0;
			System.out.print("inconsistentSupportMutex mutexes at Level : \n\n");
			for(ArrayList is:inconsistentSupportMutex) {
				System.out.print( i+": ");	
				System.out.println(is);
				i++;
			}

			System.out.println();
			i = 0;
			System.out.print("Competing Nodes mutexes at Level : \n\n");

			for(ArrayList cn:competingNodeMutex) {
				System.out.print(i+": ");	
				System.out.println(cn);
				i++;
			}
			System.out.println();
			System.out.print("Negted Literal mutexes at Level : \n\n");
			i = 0;
			for(ArrayList nl:negatedLiteralsMutex) {
				System.out.print(i+": ");	
				System.out.println(nl);
				i++;
			}
			
			System.out.println();
			System.out.println("**************************************SOLUTION PATH **************************************\n\n"+solutionPath);
			
		}

		public static void findPath(Set<Layer> actionsPath, ArrayList<Set<Layer>> allActionss,
			ArrayList<ArrayList> actionMutexesWithLevels2, Set<String> statesForPlan) {
			
			int i = 0,flag2=0;
			
			ArrayList temp = new ArrayList<>();
			ArrayList temp1;
			Iterator<Layer> itr = actionsPath.iterator();
			while(itr.hasNext()) {
				Layer a = itr.next();
				if(allActionss.get(i).contains(a)) {
					Iterator<Layer> itr2 = actionsPath.iterator();
					while(itr2.hasNext()) {
						Layer b = itr2.next();
						temp = new ArrayList<>();
						if(!(a.actionName).equals(b.actionName)) {
							
							temp.add(a.actionName);
							temp.add(b.actionName);
							if(actionMutexesWithLevels2.get(i).contains(temp)) {
								if(statesForPlan.containsAll(a.preconditionList)) {
									itr.remove();
									statesForPlan.removeAll(a.preconditionList);
									statesForPlan.addAll(a.effectList);
									solutionPath.add(a.actionName);
									i++;
									break;
								}
								else if(statesForPlan.containsAll(b.preconditionList)) {
									itr.remove();
									statesForPlan.removeAll(b.preconditionList);
									statesForPlan.addAll(b.effectList);
									solutionPath.add(b.actionName);
									i++;
									break;
								}
								else {
									temp1 = new ArrayList<>(a.preconditionList);
									temp1.removeAll(statesForPlan);
									for(Layer n:allActionss.get(i)) {
										if(!n.actionName.contains("No-op") && n.effectList.containsAll(temp1) && (n.preconditionList.size() == 1 || flag2==1)) {
											flag2 = 1;
											if(!solutionPath.contains(n.actionName)) {
												solutionPath.add(n.actionName);
												}
											statesForPlan.removeAll(b.preconditionList);
											statesForPlan.addAll(b.effectList);
											solutionPath.add(a.actionName);
											solutionPath.add(b.actionName);
											i++;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}

	
		
		public static void findMutexes(Set<Layer> set, Set<String> set2, ArrayList<ArrayList> actionMutex) {
			// TODO Auto-generated method stub
			int flag = 0,flag2 = 0;
			ArrayList<String> temp1 = new ArrayList<>();
			ArrayList<String> temp2 = new ArrayList<>();
			ArrayList temp3 = new ArrayList<>();
			ArrayList temp4 = new ArrayList<>();
			ArrayList temp5 = new ArrayList<>();
			ArrayList temp6 = new ArrayList<>();
			
			for(String a:set2) {
				for(String b:set2) {
					flag = 0;
					temp1 = new ArrayList<>();
					temp2 = new ArrayList<>();
					if(!a.equals(b)) {
						for(Layer n:set) {
								if(n.effectList.contains(a)) {
								temp1.add(n.actionName);
							}
								if(n.effectList.contains(b))
									temp2.add(n.actionName);
							
						}
						temp3 = new ArrayList<>(temp1);
						temp3.retainAll(temp2);					
						if(temp3.size()>0) {
							continue;
						}
						else {
							for(String n:temp1) {
								for(String m:temp2) {
									temp4 = new ArrayList<>();								
									if(!n.equals(m)) {
										temp4.add(n);
										temp4.add(m);									
										if(actionMutex.contains(temp4)) {
											continue;
										}
										else {
											flag = 1;
											break;
											}
									}
								}
							}
							if(flag == 0) {
								temp5.add(a);
								temp5.add(b);
								temp6.add(temp5);
								temp5 = new ArrayList<>();
							}
						}
					}
				}
			}
			inconsistentSupportMutex.add(temp6);
		}
		public static void findNextStates(ArrayList<Set<Layer>> allActionss, ArrayList<Set<String>> stateLayers) {
			Set<String> temp = new HashSet<>();
			for(Layer n:allActionss.get(allActionss.size()-1)) {
				temp.addAll(n.effectList);
			}
			stateLayers.add(temp);
		}

		public static void competingNeeds(Set<Layer> set) {
			// TODO Auto-generated method stub
			ArrayList temp = new ArrayList<>();
			ArrayList temp2 = new ArrayList<>();
			for(Layer n:set) {
				for(Layer m:set) {
					if(!m.actionName.equals(n.actionName)) {
						for(String ef:n.preconditionList) {
							if(ef.contains("~")) {
							if(m.preconditionList.contains(ef.substring(1,ef.length()))) {
								temp2.add(m.actionName);
								temp2.add(n.actionName);
								temp.add(temp2);
							}
							}
							else {
								if(m.preconditionList.contains("~"+ef)) {
									temp2.add(m.actionName);
									temp2.add(n.actionName);
									temp.add(temp2);
								}
							}
						}
						
						temp2 = new ArrayList<>();
					}
				}
			}
			competingNodeMutex.add(temp);
		}

		public static void interferenceCheck(Set<Layer> set) {
			// TODO Auto-generated method stub
			ArrayList temp = new ArrayList<>();
			ArrayList temp2 = new ArrayList<>();
			for(Layer n:set) {
				for(Layer m:set) {
					if(!m.actionName.equals(n.actionName)) {
						for(String ef:n.effectList) {
							if(ef.contains("~")) {
								
							if(m.preconditionList.contains(ef.substring(1,ef.length()))) {
								temp2.add(m.actionName);
								temp2.add(n.actionName);
								temp.add(temp2);
							}
							}
							else {
								if(m.preconditionList.contains("~"+ef)) {
									temp2.add(m.actionName);
									temp2.add(n.actionName);
									temp.add(temp2);
								}
							}
						}
						
						temp2 = new ArrayList<>();
					}
				}
			}
			interferenceMutex.add(temp);
		}

		public static void inconsistentEffectsCheck(Set<Layer> set, Set<String> set2) {
			ArrayList temp = new ArrayList<>();
			ArrayList temp2 = new ArrayList<>();
			HashSet<String> ieSet = new HashSet<String>();
			for(Layer n:set) {
				for(Layer m:set) {
					if(!m.actionName.equals(n.actionName)) {
						for(String ef:n.effectList) {
							
							if(ef.contains("~")) {
								if(m.effectList.contains(ef.substring(1,ef.length())) && !ieSet.contains(n.actionName + m.actionName)) {
									temp2.add(m.actionName);
									temp2.add(n.actionName);
									ieSet.add(m.actionName + n.actionName);
									temp.add(temp2);
								}
							}
							else {
								if(m.effectList.contains("~"+ef) && !ieSet.contains(n.actionName + m.actionName)) {
									temp2.add(m.actionName);
									temp2.add(n.actionName);
									ieSet.add(m.actionName + n.actionName);
									temp.add(temp2);
								}
							}
						}
						
						temp2 = new ArrayList<>();
					}
				}
			}
			inconsistentEffectsMutex.add(temp);
		
		}

		public static void negatedLiteralsCheck(Set<String> set) {
			ArrayList temp = new ArrayList<>();
			ArrayList temp2 = new ArrayList<>();
			for(String s:set) {
				if(set.contains("~"+s)){
					temp2.add(s);
					temp2.add("~"+s);
					temp.add(temp2);
				}
				temp2 = new ArrayList<>();
			}
			negatedLiteralsMutex.add(temp);
		
		}
	}

