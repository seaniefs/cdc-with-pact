package au.com.dius.pactworkshop.springbootprovider;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

  @RequestMapping("/provider.json")
  public Map<String, Serializable> providerJson(@RequestParam(required = false) String validDate) {
    if (validDate != null && validDate.trim().length() > 0) {
      if (DataStore.INSTANCE.getDataCount() > 0) {
        try {
          OffsetDateTime validTime = OffsetDateTime.parse(validDate);
          Map<String, Serializable> map = new HashMap<>(3);
          map.put("test", "NO");
          map.put("validDate", validDate);
          map.put("count", DataStore.INSTANCE.getDataCount());
          return map;
        } catch (DateTimeParseException e) {
          throw new InvalidQueryParameterException("'" + validDate + "' is not a date", e);
        }
      } else {
        throw new NoDataException();
      }
    } else {
      throw new QueryParameterRequiredException("validDate is required");
    }
  }
}
