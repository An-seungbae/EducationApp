package externaldatabaseconnector.utils;

import com.mendix.logging.ILogNode;

import externaldatabaseconnector.database.constants.IMxDatabaseTypes;
import externaldatabaseconnector.pojo.QueryDetails;
import externaldatabaseconnector.pojo.QueryParameter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryUtils {

  // Replace all parameters in a query with '?'
  private static String replaceQueryParameterPlaceholders(QueryDetails queryDetails, String databaseType) {
    String query = queryDetails.getQuery();

    if (query == null || query.trim().isEmpty()) {
      return null;
    }

    //Remove comments from the query
    query = removeCommentsFromQuery(query);

    // Remove the semicolon from the end of the query, if present.
    if (query.endsWith(";")) {
      query = query.substring(0, query.length() - 1);
    }

    // Replace {parameter} placeholders with '?'
    for (QueryParameter queryParameter : queryDetails.getQueryParameters()) {
      String parameterRegex = "\\{" + Pattern.quote(queryParameter.getName()) + "\\}";

      // Compile the regex with case-insensitive flag
      Pattern pattern = Pattern.compile(parameterRegex, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(query);

      // Replace all occurrences with '?'
      query = matcher.replaceAll("?");
    }

    // Add curly braces at the beginning and end of MSSQL callable queries that start with "call".
    if (query.trim().toLowerCase().startsWith("call") && databaseType.equals(IMxDatabaseTypes.MSSQL)) {
      query = "{" + query.trim() + "}";
    }

    return query.trim();
  }

  // Remove comments from the query
  private static String removeCommentsFromQuery(String query) {
    // Regex pattern to match single-line and multi-line comments
    String pattern = "(--.*?$)|(/\\*.*?\\*/)";
    Pattern regex = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL);
    Matcher matcher = regex.matcher(query);

    // Remove all comments
    return matcher.replaceAll("").trim();
  }

  private static QueryDetails orderQueryParameters(QueryDetails queryDetails) {
    String query = queryDetails.getQuery();

    if (query == null || query.trim().isEmpty()) {
      return queryDetails;
    }

    // Extract placeholders from the query in order of appearance
    List<String> placeholderOrder = extractPlaceholders(query);

    // Map to quickly lookup parameters by name
    Map<String, QueryParameter> parameterMap = new HashMap<>();
    for (QueryParameter queryParameter : queryDetails.getQueryParameters()) {
      parameterMap.put(queryParameter.getName().toLowerCase(), queryParameter);
    }

    // Rearrange parameters based on the placeholder order
    List<QueryParameter> sortedParameters = new ArrayList<>();
    for (String placeholder : placeholderOrder) {
      QueryParameter parameter = parameterMap.get(placeholder.toLowerCase());
      if (parameter != null) {
        sortedParameters.add(parameter);
      }
    }

    // Update the QueryDetails object with the sorted parameters
    queryDetails.setQueryParameters(sortedParameters);
    return queryDetails;
  }

  private static List<String> extractPlaceholders(String query) {
    List<String> placeholders = new ArrayList<>();
    Pattern pattern = Pattern.compile("\\{(\\w+)\\}"); // Match {paramName}
    Matcher matcher = pattern.matcher(query);

    while (matcher.find()) {
      placeholders.add(matcher.group(1)); // Extract the placeholder name without braces
    }

    return placeholders;
  }

  public static QueryDetails getUpdatedQueryDetails(QueryDetails queryDetails, String databaseType, ILogNode logNode) {
    QueryDetails updatedQueryDetails = orderQueryParameters(queryDetails);

    //Convert date and time to epoch time to process the query
    convertDateTimeToEpoch(updatedQueryDetails.getQueryParameters(), logNode);

    String replaceQuery = replaceQueryParameterPlaceholders(updatedQueryDetails, databaseType);
    updatedQueryDetails.setQuery(replaceQuery);
    return updatedQueryDetails;
  }

  private static void convertDateTimeToEpoch(List<QueryParameter> queryParameters, ILogNode logNode) {
    SimpleDateFormat dateFormatWithSeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");// Format with seconds

    for (QueryParameter queryParameter : queryParameters) {
      String parameterValue = queryParameter.getValue();

      if(!queryParameter.getDataType().equals("DATETIME") || parameterValue.isBlank() || parameterValue.equals("null"))
        continue;
      
        try {
          long epochMillis = dateFormatWithSeconds.parse(parameterValue).getTime();
          queryParameter.setValue(String.valueOf(epochMillis));
        } catch (ParseException exception) {
          logNode.error(exception.getMessage(), exception);
        }
    }
  }
}
