package com.springapp.exceptions;

import java.util.ArrayList;

/**
 * Created by franschl on 06.04.15.
 */
public class ConstraintViolatedException {
    private ArrayList<String> messages = new ArrayList<>();

    public void addMessage(String message) {
        messages.add(message);
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
