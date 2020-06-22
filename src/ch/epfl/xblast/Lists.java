package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Lists {

    /**
     * Constructor of the Lists class.
     * 
     */
    private Lists() { }

    /**
     * Method that returns a List of the generic type T which is created through
     * the concatenation of the given List and its mirror.
     * 
     * @param l
     * @throws IllegalArgumentException is l is empty
     * @return List<T>
     */
    public static <T> List<T> mirrored(List<T> l) throws IllegalArgumentException {
        if (l.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<T> mirroredList1 = new ArrayList<T>(l);
        List<T> mirroredList2 = new ArrayList<T>(l);
        Collections.reverse(mirroredList2);
        mirroredList2 = mirroredList2.subList(1, mirroredList2.size());
        mirroredList1.addAll(mirroredList2);
        return mirroredList1;
    }

    /**
     * This method returns all the possible permutations for a given list l.
     * 
     * @param l
     * @return List<List<T>> all of the permutations for a given list l
     */
    public static <T> List<List<T>> permutations(List<T> l) {
        List<List<T>> recursionBase = new ArrayList<List<T>>();
        if (l.isEmpty()) {
            recursionBase.add(new ArrayList<T>(Collections.emptyList()));
            return recursionBase;
        } else {
            T head = l.get(0);
            List<List<T>> recursionStep = permutations(l.subList(1, l.size()));
            List<List<T>> permutation = new ArrayList<List<T>>(recursionStep.size() + 1);
            for (List<T> eachL : recursionStep) {
                for (int i = 0; i <= eachL.size(); i++) {
                    List<T> copyOfList = new ArrayList<T>(eachL);
                    copyOfList.add(i, head);
                    permutation.add(copyOfList);
                }
            }
            return Collections.unmodifiableList(new ArrayList<>(permutation));
        }
    }
}