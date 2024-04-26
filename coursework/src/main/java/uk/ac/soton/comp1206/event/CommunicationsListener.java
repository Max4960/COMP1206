package uk.ac.soton.comp1206.event;

/**
 * The Communications Listener is used for listening to messages received by the communicator.
 *
 * @author ASUS
 * @version $Id: $Id
 */
public interface CommunicationsListener {

    /**
     * Handle an incoming message received by the Communicator
     *
     * @param communication the message that was received
     */
    public void receiveCommunication(String communication);
}
