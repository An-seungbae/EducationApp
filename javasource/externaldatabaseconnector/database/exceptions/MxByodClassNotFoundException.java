package externaldatabaseconnector.database.exceptions;

import externaldatabaseconnector.exceptions.MxException;

import java.util.Map;

public class MxByodClassNotFoundException extends MxException {

  public MxByodClassNotFoundException(String message) {
    super(message);
  }

  public MxByodClassNotFoundException(String msg, String messageCode, Map<String, String> messageDataMap) {
    super(msg, messageCode, messageDataMap);
  }

  public MxByodClassNotFoundException(String message, Throwable e) {
    super(message, e);
  }
}
