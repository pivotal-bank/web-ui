package io.pivotal.web.service;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class FlashService {

    public static String redirectWithMessage(String redirectUrl, String message) {
        return "redirect:" + redirectUrl + "?successMessage=" + message;
    }

    public static void errorMessage(String message) {
        RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
        ra.setAttribute("errorMessage", message, RequestAttributes.SCOPE_REQUEST);
    }


    public static boolean hasErrorMessage() {
        RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
        return ra.getAttribute("errorMessage", RequestAttributes.SCOPE_REQUEST) != null;
    }

    public static String getErrorMessage() {
        RequestAttributes ra = RequestContextHolder.currentRequestAttributes();
        return (String) ra.getAttribute("errorMessage", RequestAttributes.SCOPE_REQUEST);
    }

}
