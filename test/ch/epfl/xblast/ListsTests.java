package ch.epfl.xblast;

import ch.epfl.xblast.Lists;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class ListsTests {

    @Test(expected = IllegalArgumentException.class)
    public void mirroredThrowsExceptionOnEmptyList() {
        Lists.mirrored(Collections.emptyList());
    }

    @Test
    public void mirroredBehaviourIsCorrect() {
        List<Character> aibohp = stringToList("aibohp");
        List<Character> detart = stringToList("detart");
        List<Character> delev = stringToList("delev");
        List<Character> race = stringToList("race");


        assertEquals(stringToList("aibohphobia"), Lists.mirrored(aibohp));
        assertEquals(stringToList("detartrated"), Lists.mirrored(detart));
        assertEquals(stringToList("deleveled"), Lists.mirrored(delev));
        assertEquals(stringToList("racecar"), Lists.mirrored(race));
    }

    @Test
    public void singletonListMirroredRemainsUnchanged() {
        List<Character> a = Collections.singletonList('a');
        assertEquals(a, Lists.mirrored(new ArrayList<>(a)));
    }

    @Test
    public void permutationsOnEmptyListIsCorrect() {
        assertEquals(Collections.singletonList(Collections.emptyList()),
                Lists.permutations(new ArrayList<>()));
    }

    @Test
    public void permutationsOnSingleElementIsCorrect() {
        assertEquals(Collections.singletonList(Collections.singletonList(1)),
                Lists.permutations(new ArrayList<>(Collections.singletonList(1))));
    }

    @Test
    public void permutationsOnTwoElementsIsCorrect() {
        List<Integer> l = Arrays.asList(1, 2);
        Set<List<Integer>> s = new HashSet<>(Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(2, 1)
        )
        );

        assertEquals(s, new HashSet<>(Lists.permutations(new ArrayList<>(l))));
    }

    @Test
    public void permutationsOnMoreElementsIsCorrect() {
        List<Integer> l = Arrays.asList(1, 2, 3, 4);
        Set<List<Integer>> s = new HashSet<>(Arrays.asList(
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(1, 2, 4, 3),
                Arrays.asList(1, 3, 2, 4),
                Arrays.asList(1, 3, 4, 2),
                Arrays.asList(1, 4, 2, 3),
                Arrays.asList(1, 4, 3, 2),
                Arrays.asList(2, 1, 3, 4),
                Arrays.asList(2, 1, 4, 3),
                Arrays.asList(2, 3, 1, 4),
                Arrays.asList(2, 3, 4, 1),
                Arrays.asList(2, 4, 1, 3),
                Arrays.asList(2, 4, 3, 1),
                Arrays.asList(3, 1, 2, 4),
                Arrays.asList(3, 1, 4, 2),
                Arrays.asList(3, 2, 1, 4),
                Arrays.asList(3, 2, 4, 1),
                Arrays.asList(3, 4, 1, 2),
                Arrays.asList(3, 4, 2, 1),
                Arrays.asList(4, 1, 2, 3),
                Arrays.asList(4, 1, 3, 2),
                Arrays.asList(4, 2, 1, 3),
                Arrays.asList(4, 2, 3, 1),
                Arrays.asList(4, 3, 1, 2),
                Arrays.asList(4, 3, 2, 1)
        )
        );

        assertEquals(s, new HashSet<>(Lists.permutations(new ArrayList<>(l))));
    }

    private List<Character> stringToList(String s) {
        List<Character> l = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            l.add(s.charAt(i));
        }
        return l;
    }
}
