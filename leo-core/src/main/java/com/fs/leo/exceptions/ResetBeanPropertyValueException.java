package com.fs.leo.exceptions;

import org.springframework.beans.BeansException;

/**
 * Created by fanshuai on 16/7/2.
 */
public class ResetBeanPropertyValueException extends BeansException {
    public ResetBeanPropertyValueException(String msg) {
        super(msg);
    }

    public ResetBeanPropertyValueException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
