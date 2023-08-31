package ru.hogwarts.school.utility;

public class MessageGenerator {
    public static String getMsgIfMethodInvoked(String methodName) {
        return String.format("Method '%s' has been invoked", methodName);
    }
}
