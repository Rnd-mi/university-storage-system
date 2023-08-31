package ru.hogwarts.school.utility;

public class MessageGenerator {

    public static String getMsgIfMethodInvoked(String methodName) {
        return String.format("Method '%s' was invoked", methodName);
    }

    public static String getMsgWhenException(String exceptionName) {
        return String.format("Exception '%s' was thrown", exceptionName);
    }

    public static String getMsgWhenException(String exceptionName, long id) {
        return String.format("Exception '%s' was thrown. Id = %d", exceptionName, id);
    }

    public static String getMsgWhenException(String exceptionName, Object object) {
        return String.format("Exception '%s' was thrown. %s", exceptionName, object);
    }
}
