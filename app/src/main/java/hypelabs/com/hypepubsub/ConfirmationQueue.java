package hypelabs.com.hypepubsub;


import com.hypelabs.hype.Hype;

import java.io.IOException;
import java.util.ArrayList;

public class ConfirmationQueue
{
    final private static ConfirmationQueue confirmationQueue = new ConfirmationQueue(); // Early loading to avoid thread-safety issues

    private ArrayList<ConfirmationQueueElement> elements;

    private ConfirmationQueue()
    {
        elements = new ArrayList<>();
    }

    public static ConfirmationQueue getInstance()
    {
        return confirmationQueue;
    }

    public int addQueueElement(ConfirmationQueueElement element) throws IOException
    {
        if(isElementInQueue(element)){
            return -1;
        }

        elements.add(element);
        return 0;
    }

    public int removeQueueElement(ConfirmationQueueElement element)
    {
        elements.remove(element);
        return 0;
    }

    public boolean isElementInQueue(ConfirmationQueueElement element) throws IOException
    {
      for( ConfirmationQueueElement queueElement : elements)
      {
          if(queueElement.hpbMessage.toByteArray().equals(element.hpbMessage.toByteArray())
                  && queueElement.destination.getIdentifier().equals(element.destination.getIdentifier()))
          {
              return true;
          }
      }

      return false;
    }

    public ConfirmationQueueElement findQueueElementFromId(int identifier)
    {
        for( ConfirmationQueueElement queueElement : elements)
        {
            if(queueElement.identifier == identifier){
                return queueElement;
            }
        }

        return null;
    }

    public int removeQueueElementFromId(int identifier)
    {
        ConfirmationQueueElement queueElement = findQueueElementFromId(identifier);

        if(queueElement == null)
            return -1;

        elements.remove(queueElement);
        return 0;
    }
}

