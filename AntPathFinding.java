import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntPathFinding {

    private static final int MAX_ITERATIONS = 100;
    private static final int NUM_ANTS = 10;
    private static final double ALPHA = 1.0; // pheromone importance
    private static final double BETA = 2.0; // heuristic information importance
    private static final double RHO = 0.5; // evaporation rate
    private static final double Q = 100; // pheromone deposit factor

    private final int[][] graph; // adjacency matrix representing the graph
    private final double[][] pheromone; // pheromone matrix
    private final int numNodes;

    public AntPathFinding(int[][] graph) {
        this.graph = graph;
        this.numNodes = graph.length;
        pheromone = new double[numNodes][numNodes];
        // Initialize pheromone levels
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                pheromone[i][j] = 1.0;
            }
        }
    }

    // Method to find the best path using Ant Colony Optimization algorithm
    public List<Integer> findPath() {
        List<Integer> bestPath = null;
        double bestPathLength = Double.MAX_VALUE;

        // Perform iterations
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            List<List<Integer>> antPaths = new ArrayList<>();
            double[] antPathLengths = new double[NUM_ANTS];

            // Generate paths for each ant
            for (int j = 0; j < NUM_ANTS; j++) {
                List<Integer> antPath = generateAntPath();
                double pathLength = calculatePathLength(antPath);
                antPaths.add(antPath);
                antPathLengths[j] = pathLength;

                // Updates the best path if a shorter path is found
                if (pathLength < bestPathLength) {
                    bestPath = antPath;
                    bestPathLength = pathLength;
                }
            }

            // Update pheromones based on the paths of ants
            updatePheromones(antPaths, antPathLengths);
        }

        return bestPath;
    }

    // Method to generate a path for a single ant
    private List<Integer> generateAntPath() {
        Random rand = new Random();
        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[numNodes];
        int currentNode = rand.nextInt(numNodes);

        path.add(currentNode);
        visited[currentNode] = true;

        // Choose the next node until all nodes are visited
        while (path.size() < numNodes) {
            int nextNode = selectNextNode(currentNode, visited);
            path.add(nextNode);
            visited[nextNode] = true;
            currentNode = nextNode;
        }

        return path;
    }

    // Method to select the next node for the ant's path
    private int selectNextNode(int currentNode, boolean[] visited) {
        double[] probabilities = new double[numNodes];
        double probabilitiesSum = 0;

        // Calculate probabilities for unvisited nodes
        for (int i = 0; i < numNodes; i++) {
            if (!visited[i]) {
                probabilities[i] = Math.pow(pheromone[currentNode][i], ALPHA)
                        * Math.pow(1.0 / graph[currentNode][i], BETA);
                probabilitiesSum += probabilities[i];
            }
        }

        // Select the next node based on probabilities
        double rand = Math.random() * probabilitiesSum;
        double sum = 0;
        for (int i = 0; i < numNodes; i++) {
            if (!visited[i]) {
                sum += probabilities[i];
                if (sum >= rand) {
                    return i;
                }
            }
        }

        throw new RuntimeException("Should not happen");
    }

    // Method to calculate the length of a path
    private double calculatePathLength(List<Integer> path) {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            length += graph[path.get(i)][path.get(i + 1)];
        }
        return length;
    }

    // Method to update pheromone levels
    private void updatePheromones(List<List<Integer>> antPaths, double[] antPathLengths) {
        // Evaporation
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                pheromone[i][j] *= (1 - RHO);
            }
        }

        // Pheromone deposit
        for (int k = 0; k < NUM_ANTS; k++) {
            List<Integer> antPath = antPaths.get(k);
            double antPathLength = antPathLengths[k];
            double pheromoneDeposit = Q / antPathLength;

            for (int i = 0; i < antPath.size() - 1; i++) {
                int from = antPath.get(i);
                int to = antPath.get(i + 1);
                pheromone[from][to] += pheromoneDeposit;
                pheromone[to][from] += pheromoneDeposit; // Assume undirected graph
            }
        }
    }

    public static void main(String[] args) {
        // Example graph representation (distance between nodes)
        int[][] graph = {
                {0, 2, 4, 0, 0},
                {2, 0, 1, 5, 0},
                {4, 1, 0, 8, 2},
                {0, 5, 8, 0, 3},
                {0, 0, 2, 3, 0}
        };

        AntPathFinding antPathFinding = new AntPathFinding(graph);
        List<Integer> path = antPathFinding.findPath();

        System.out.println("Best path found: " + path);
    }
}
