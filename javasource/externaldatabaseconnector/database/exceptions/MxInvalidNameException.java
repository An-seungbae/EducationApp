package externaldatabaseconnector.database.exceptions;

import externaldatabaseconnector.exceptions.MxException;

import java.util.Map;

public class MxInvalidNameException extends MxException {

  public MxInvalidNameException(String msg) {
    super(msg);
  }

  public MxInvalidNameException(String msg, String messageCode, Map<String, String> messageDataMap) {
    super(msg, messageCode, messageDataMap);
  }

  public MxInvalidNameException(String msg, Throwable throwable) {
    super(msg, throwable);
  }
}
