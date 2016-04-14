package net.catten.importer.checker.core.com;

/**
 * Created by Catten on 2016/4/14.
 */
public class FieldFormatRangeCompare implements Compare {
    private int min;
    private int max;
    private String splitChar;

    public FieldFormatRangeCompare(String splitChar, int minCount, int macCount){
        this.splitChar = splitChar;
        min = minCount;
        max = macCount;
    }

    @Override
    public boolean compare(String input) {
        try {
            String[] s = input.split(splitChar);
            return !(s.length > max || s.length < min);
        }catch (Throwable t){
            return false;
        }
    }
}
