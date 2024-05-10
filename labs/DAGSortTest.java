import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DAGSortTest {

    @Test
    public void testBasicCase() throws InvalidNodeException, CycleDetectedException {
        int[][] edges = new int[][]{{3}, {3,4}, {4,7}, {5,6,7}, {6},{},{},{}};
        int[] solution = DAGSort.sortDAG(edges);
        //int[] solution = {0, 1, 3, 2, 4, 5, 7, 6};
        System.out.println("Solution Provided: " + Arrays.toString(solution));
        System.out.println("");
        boolean valid = false; // Set to false for case when solution = []
        for (int i = 1; i < solution.length; i++) { // No point starting at 0 --- would just have to add another break case
            System.out.println("Node: " + i);
            if (solution[i] > 7 || solution[i] < 0) { // Assuming nodes are in the range 0 -> the highest value in edges
                valid = false;                        // This isn't a great solution as if, say, there is no node 5 it will still continue
                break;                                // But it avoids even more for loops
            }

            if (i == 1) { // Want to assume valid on first iteration, so we don't accidentally break out
                valid = true; // This might be redundant...
            }

            int[] subArray = Arrays.copyOfRange(solution, 0, i);
            System.out.println("-> Sub-Array: " + Arrays.toString(subArray));
            int[] invalids = edges[i];
            System.out.println("-> Invalids: " + Arrays.toString(invalids));
            System.out.println();
            for (int j = 0; j < invalids.length; j++) {
                for (int k = 0; k < subArray.length; k++) {
                    if (invalids[j] == subArray[k]) {
                        System.out.println("Equal Values: " + invalids[j] + " == " + subArray[k]);
                        valid = false;
                        break;
                    } else {
                        valid = true;
                    }
                }
                if (!valid) {
                    break;
                }
            }
            if (!valid) {
                break;
            }
        }
        assertTrue(valid);
    }

    @Test
    public void testEmptyCase() throws InvalidNodeException, CycleDetectedException {
        int[][] edges = new int[][]{};
        //int[] solution = new int[]{};
        //assertArrayEquals(DAGSort.sortDAG(edges), solution);
        assertTrue(DAGSort.sortDAG(edges).length == 0);
    }

    @Test
    public void testNullPointerExceptionCase() throws InvalidNodeException, CycleDetectedException {
        assertThrows(NullPointerException.class, () -> DAGSort.sortDAG(null));
    }

    @Test
    public void testCycleCase() throws InvalidNodeException, CycleDetectedException {
        int[][] edges = new int[][]{{1},{2},{0}};
        assertThrows(CycleDetectedException.class, () -> DAGSort.sortDAG(edges));
    }

    @Test
    public void testInvalidNodeCase() throws InvalidNodeException, CycleDetectedException {
        int[][] edges = new int[][]{{1},{2},{3}};
        assertThrows(InvalidNodeException.class, () -> DAGSort.sortDAG(edges));
    }

    @Test
    public void testChainedCase() throws InvalidNodeException, CycleDetectedException {
        int[][] edges = new int[][]{{1},{2},{3},{4},{5},{}};
        int[] solution = new int[]{0,1,2,3,4,5};
        assertArrayEquals(solution, DAGSort.sortDAG(edges));
    }

    @Test
    public void testNegativeCase() throws InvalidNodeException, CycleDetectedException {
        int[][] edges = new int[][]{{-1},{-2},{0}};
        assertThrows(InvalidNodeException.class, () -> DAGSort.sortDAG(edges));
    }

    @Test
    public void testSingleCase() throws InvalidNodeException, CycleDetectedException {
        int[][] edges = new int[][]{{}};
        int[] solution = new int[]{0};
        assertArrayEquals(solution, DAGSort.sortDAG(edges));
    }
}
