package ru.hogwarts.school.utility;

public class MessageGenerator {

    public static String generateMsgIfMethodInvoked(String methodName) {
        return String.format("Method '%s' was invoked", methodName);
    }

    public static String generateMsgWhenException(String exceptionName) {
        return String.format("Exception '%s' was thrown", exceptionName);
    }

    public static String generateMsgWhenException(String exceptionName, long id) {
        return String.format("Exception '%s' was thrown. Id = %d", exceptionName, id);
    }

    public static String generateMsgWhenException(String exceptionName, Object object) {
        return String.format("Exception '%s' was thrown. %s", exceptionName, object);
    }
}
