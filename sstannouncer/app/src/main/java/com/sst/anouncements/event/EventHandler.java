package com.sst.anouncements.event;

/**
 * Event Handler
 * Defines an interface representing a event handler.
 */
public interface EventHandler {
    /**
     * Event Handling Method
     * The user defines this method to handle a event that was raised.
     *
     * @param event The event that triggered the handler.
     */
    public void handle(Event event);
}

