package net.catten.importer.checker.core.com;

/**
 * Created by Catten on 2016/4/14.
 */
public class RegexCompare implements Compare {
    private String regex;

    public RegexCompare(String regex){
        this.regex = regex;
    }

    public boolean compare(String input){
        return input.matches(regex);
    }
}
