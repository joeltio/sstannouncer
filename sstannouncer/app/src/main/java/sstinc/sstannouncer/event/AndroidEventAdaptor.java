package sstinc.sstannouncer.event;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Bundle;
import android.os.RemoteException;

import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Android Event Adaptor
 * Defines an Adaptor Event Controller and Android Messenger System.
 * allowing events to be transmitted across processes.
 * Enables the transmission of Events through the Android OS.
 *
 * Connection
 * The Adaptor is able to only able to transmit events when connected.
 * The adaptor only functions after the <code>localMessenger</code> and
 * <code>remoteMessenger</code> has been set to enable communication between Android Event
 * Adaptors.
 * The state of being connected is defined by the method <cod>connected()</cod>
 *
 * @see Event
 * @see EventController
 * @see AndroidEventAdaptor#connected()
 * @see android.os.Messenger
 */
public class AndroidEventAdaptor {
    /**
     * Message Handler
     * Message Handler for use with Messenger for inter process communication.
     */
    public static class MessageHandler extends Handler {
        private WeakReference<AndroidEventAdaptor> eventAdaptor;

        /**
         * Message Handler Constructor
         * Create a new instance of the handler class.
         * The handler delegates the handling process to the eventAdaptor
         *
         * @param eventAdaptor
         */
        MessageHandler(AndroidEventAdaptor eventAdaptor)
        {
            this.eventAdaptor = new WeakReference<AndroidEventAdaptor>(eventAdaptor);
        }

        @Override
        public void handleMessage(Message msg) {
            this.eventAdaptor.get().handleMessage(msg);
        }
    }


    //Message Subject
    public static int MESSAGE_INIT_CONNECTION = 0; //Message to initialise a connection.
    public static int MESSAGE_TERM_CONNECTION = 1; //Message to terminate a connection.
    public static int MESSAGE_PING_CONNECTION = 2; //Message to test a connection;
    public static int MESSAGE_PING_ACK = 3; //Message to acknowledge a ping
    public static int MESSAGE_SEND_EVENT = 4; //Message to send a event.

    //Message Data
    public static String MESSAGE_DATA_MESSENGER = "msg.data.messenger";
    public static String MESSAGE_DATA_EVENT = "msg.data.event";

    private EventController boundEventController;
    private Messenger localMessenger; //Messenger to receive messages sent the the local process.
    private Messenger remoteMessenger; //Messenger to send messages to remote process

    //Ping Data
    private Thread pingThread;
    private boolean pingResult;

    /**
     * Android Event Adaptor
     * Creates a new Android Event Adaptor.
     * The adaptor facilitates the transmission of events between the <code>eventController</code>
     * and the Android Messenger System.
     * NOTE: That the adaptor only functions after the <code>localMessenger</code> and
     * <code>remoteMessenger</code> has been set to enable communicate between Android Event
     * Adaptors.
     *
     * @param eventController The Event Controller to bind to.
     */
    public AndroidEventAdaptor(EventController eventController)
    {
        this.boundEventController = eventController;

        AndroidEventAdaptor.MessageHandler eventAdaptor =
                new AndroidEventAdaptor.MessageHandler(this);
        this.localMessenger = new Messenger(eventAdaptor);
    }


    /**
     * Determines if the Adaptor is connected.
     * Determines if the Adaptor is connected and able to transmit and recieve events;
     * Triggers disconnect if ping is not successful.
     *
     * @return Returns true if the Adaptor is connected, false otherwise.
     */
    public boolean connected()
    {
        this.pingThread = Thread.currentThread();
        this.pingResult = false;

        this.sendPing();

        try
        {
            Thread.currentThread().wait();
        }
        catch(InterruptedException exp)
        {

        }

        if(this.pingResult == true)
        {
            //Ping was successful
            return true;
        }
        else
        {
            //Ping was failure
            return false;
        }
    }

    /**
     * Attempt to connect the adaptor.
     * Attempt to connect with the remote process for <code>remoteMessenger</code> and the bound
     * event controller.
     *
     * @param remoteMessenger The messenger to use to communicate with the remote process.
     *
     * @return Returns if the connection attempt was successful, false otherwise
     */
    public boolean connect(Messenger remoteMessenger)
    {
        //Listen to local Event Controller
        final AndroidEventAdaptor adaptor = this;
        this.boundEventController.listen(this.toString(), "*", new EventHandler() {
            @Override
            public void handle(Event event) {
                adaptor.sendEvent(event);
            }
        });

        //Connect to Remote Process
        this.remoteMessenger = remoteMessenger;
        this.sendInitMessage();

        return this.connected();

    }

    /**
     * Disconnect from the adaptor
     * Disconnect from the remote process and bound event controller.
     *
     */
    public void disconnect()
    {
        this.boundEventController.unlisten(this.toString(), "*");

        this.sendTermMessage();
        this.remoteMessenger = null;
    }

    /**
     * Get the Local Messenger
     * Retrieve the Messenger to receive messages sent to the local process. (ie. Inbox)
     *
     * @return Returns the Local Messenger.
     */
    public Messenger getLocalMessenger() {
        return localMessenger;
    }


    //Private Utility Methods
    private void handleMessage(Message message) {
        if (message.what == AndroidEventAdaptor.MESSAGE_INIT_CONNECTION) {
            Messenger remoteMessenger =
                    message.getData().getParcelable(AndroidEventAdaptor.MESSAGE_DATA_MESSENGER);
            this.remoteMessenger = remoteMessenger;
        }

        if(message.what == AndroidEventAdaptor.MESSAGE_TERM_CONNECTION)
        {
            this.remoteMessenger = null;
        }


        if(message.what == AndroidEventAdaptor.MESSAGE_PING_CONNECTION)
        {
            this.receivePing();
        }

        if(message.what == AndroidEventAdaptor.MESSAGE_PING_ACK)
        {
            this.receivePingACK();
        }

        if (message.what == AndroidEventAdaptor.MESSAGE_SEND_EVENT) {
            String eventString =
                    message.getData().getString(AndroidEventAdaptor.MESSAGE_DATA_EVENT);
            Event sentEvent = new Event(eventString);
            this.receiveEvent(sentEvent);
        }

    }

    private void receiveEvent(Event event)
    {
        this.boundEventController.raise(event);
    }

    private void sendEvent(Event event)
    {
        if(this.connected() == true)
        {
            Message eventMessage = new Message();
            eventMessage.what = AndroidEventAdaptor.MESSAGE_SEND_EVENT;
            Bundle messageData = new Bundle();
            messageData.putString(AndroidEventAdaptor.MESSAGE_DATA_EVENT, event.toString());

            try {
                this.remoteMessenger.send(eventMessage);
            } catch (RemoteException exp) {

            }
        }
    }

    private void sendPing()
    {
        if(this.remoteMessenger != null)
        {
            Message pingMessage = new Message();
            pingMessage.what = AndroidEventAdaptor.MESSAGE_PING_CONNECTION;

            try {
                this.remoteMessenger.send(pingMessage);
            }
            catch (RemoteException exp) {
            }
        }

    }

    private void receivePing()
    {
        if (this.remoteMessenger != null) {
            Message pingMessage = new Message();
            pingMessage.what = AndroidEventAdaptor.MESSAGE_PING_ACK;

            try {
                this.remoteMessenger.send(pingMessage);
            } catch (RemoteException exp) {

            }

        }
    }

    private void receivePingACK()
    {
        this.pingResult = true;
        this.pingThread.notify();
    }

    private void sendInitMessage()
    {
        if(this.remoteMessenger != null)
        {
            Message messageInit = new Message();
            messageInit.what = AndroidEventAdaptor.MESSAGE_INIT_CONNECTION;
            Bundle messageData = new Bundle();
            messageData.putParcelable(AndroidEventAdaptor.MESSAGE_DATA_MESSENGER,
                    this.getLocalMessenger());
            messageInit.setData(messageData);

            try {
                this.remoteMessenger.send(messageInit);
            } catch (RemoteException exp) {

            }
        }
    }

    private void sendTermMessage()
    {
        if(this.remoteMessenger != null)
        {
            Message messageTerm = new Message();
            messageTerm.what = AndroidEventAdaptor.MESSAGE_TERM_CONNECTION;

            try
            {
                this.remoteMessenger.send(messageTerm);
            }catch(RemoteException exp)
            {

            }
        }
    }
}


