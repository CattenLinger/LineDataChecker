package net.catten.importer.checker.core.com;

/**
 * Created by Catten on 2016/4/14.
 */
public class FieldFormatCompare implements Compare {
    private String splitChar;
    private int fieldCount;

    public FieldFormatCompare(String splitChar, int fieldCount){
        this.splitChar = splitChar;
        this.fieldCount = fieldCount;
    }

    @Override
    public boolean compare(String input) {
        try {
            String[] s = input.split(splitChar);
            return s.length == fieldCount;
        }catch (Throwable t){
            return false;
        }
    }
}
