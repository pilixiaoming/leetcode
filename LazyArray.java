package databricks;

import java.util.*;
import java.util.function.Function;

//class NoValueFoundException extends Exception {
//    public NoValueFoundException(String message) {
//        super(message);
//    }
//}

// use private constructor to avoid user config the func map
// use static build fun to construct the lazy array

public class LazyArray<T> {
    private List<T> values;
    private List<Function<T, T>> prevMapFuncs;

    public LazyArray(List<T> values) {
        this.values = values;
        this.prevMapFuncs = new ArrayList<>();
    }

    private LazyArray(List<T> values, List<Function<T, T>> prevMapFuncs) {
        this.values = values;
        this.prevMapFuncs = prevMapFuncs;
    }

    // O(1)
    public LazyArray<T> map(Function<T, T> mapFunc) {
        List<Function<T, T>> newPrevMapFuncs = new ArrayList<>(prevMapFuncs);
        newPrevMapFuncs.add(mapFunc);
        return new LazyArray<>(values, newPrevMapFuncs);
    }

    // O(N * K) - N length of values, K length of map funcs
    // what is multiple value matched?
    public int indexOf(T value) throws Exception {
        int resultIndex = -1;
        int index = 0;

        while (index < values.size()) {
            T curValue = values.get(index);

            for (Function<T, T> func : prevMapFuncs) {
                curValue = func.apply(curValue);
            }
            if (curValue.equals(value)) {
                resultIndex = index;
            }
            index++;
        }

        if (resultIndex == -1) {
            throw new Exception("No valid value found after mapped.");
        }
        return resultIndex;
    }

    public static void main(String[] args) throws Exception {
        LazyArray<Integer> lazyArray = new LazyArray(Arrays.asList(10,20,30,40,50));
//        System.out.println(lazyArray.map(e -> e * 2).indexOf(40));
//        System.out.println(lazyArray.map(e -> e * 2).indexOf(40));
//        System.out.println(lazyArray.map(e -> e * 3).indexOf(60));
        System.out.println(lazyArray.map(e -> e * 2).map(e -> e * 3).indexOf(240));
    }
}
