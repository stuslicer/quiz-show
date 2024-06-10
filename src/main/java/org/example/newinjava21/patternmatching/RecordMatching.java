package org.example.newinjava21.patternmatching;

public class RecordMatching {

    public static void main(String[] args) {


    }

    private static String getNameTypePattern(Object obj) {
        if(obj instanceof Person p) {
            return p.name();
        }
        return null;
    }

private static String getNameRecordMatching(Object obj) {
    if(obj instanceof Person(String name, int age)) {
        return name;
    }
    return null;
}
}
