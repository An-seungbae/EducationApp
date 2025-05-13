package externaldatabaseconnector.database.exceptions;

import externaldatabaseconnector.exceptions.MxException;

import java.util.Map;

public class MxInvalidParameterModeException extends MxException {

  public MxInvalidParameterModeException(String msg) {
    super(msg);
  }

  public MxInvalidParameterModeException(String msg, String messageCode, Map<String, String> messageDataMap) {
    super(msg, messageCode, messageDataMap);
  }

  public MxInvalidParameterModeException(String msg, Throwable throwable) {
    super(msg, throwable);
  }
}
