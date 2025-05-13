package externaldatabaseconnector.livepreview.responses;

import com.mendix.thirdparty.org.json.JSONObject;

import externaldatabaseconnector.database.exceptions.MxDataTypeNotSupported;
import externaldatabaseconnector.exceptions.MxException;
import externaldatabaseconnector.livepreview.constants.IMxResponses;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class BaseResponse {
  private boolean actionStatus = false;
  private boolean isConnectionSuccess = false;
  private String message;
  private String callStackTrace;

  public boolean getActionStatus() {
    return actionStatus;
  }

  public void setActionStatus(boolean actionStatus) {
    this.actionStatus = actionStatus;
  }

  public boolean isConnectionSuccess() {
    return isConnectionSuccess;
  }

  public void setConnectionSuccess(boolean connectionSuccess) {
    isConnectionSuccess = connectionSuccess;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setCallStackTrace(String callStackTrace) {
    this.callStackTrace = callStackTrace;
  }

  //TODO:Try to convert POJO into a JSON objects instead of implementing the toJSON method in other responses
  public JSONObject toJson() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(IMxResponses.STATUS, this.actionStatus);
    jsonObject.put(IMxResponses.IS_CONNECTION_SUCCESS, this.isConnectionSuccess);
    jsonObject.put(IMxResponses.MESSAGE, this.message);
    jsonObject.put(IMxResponses.STACKTRACE, this.callStackTrace);
    return jsonObject;
  }

  public void setExceptionDetails(Exception aException) {

    String stackTrace = Arrays.stream(aException.getStackTrace())
        .map(StackTraceElement::toString)
        .collect(Collectors.joining("\n"));

    if (aException instanceof MxException) {
      this.message = ((MxException) aException).getMessageExtended();
    } else {
      this.message = aException.getMessage();
    }

    this.callStackTrace = stackTrace;
  }

}
