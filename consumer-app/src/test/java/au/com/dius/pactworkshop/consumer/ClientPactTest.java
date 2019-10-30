package au.com.dius.pactworkshop.consumer;

import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// see https://github.com/DiUS/pact-jvm/tree/master/provider/pact-jvm-provider-maven

public class ClientPactTest {

  private Client client;
  private LocalDateTime date = LocalDateTime.now();

  @Rule
  public PactProviderRule provider = new PactProviderRule("ProviderMicroservice", null,
          1234, this);

  @Before
  public void setup() {
    client = new Client("http://localhost:1234");
  }

  @Pact(provider="ProviderMicroservice", consumer="ConsumerApp")
  public RequestResponsePact shouldHavePactWithOurProvider(final PactDslWithProvider builder) {
    final Map json = new HashMap<>();
    json.put("test", "NO");
    json.put("date", "2013-08-16T15:31:20Z");
    json.put("count", 100);
    final String dateString = "2013-08-16T15:31:20Z";
    return builder.given("data count > 0",json)
            .uponReceiving("a request for json data")
            .method("GET")
            .path("/provider.json")
             // NOTE: Need to be sure that you escape chars which are part of RegEx
            .matchQuery("validDate", dateString.replace(".", "\\."))
            .willRespondWith().status(200)
            .body("{\"test\": \"NO\", \"validDate\": \"2013-08-16T15:31:20Z\", \"count\": 100}")
            .toPact();
  }

  @Pact(provider="ProviderMicroservice", consumer="ConsumerApp")
  public RequestResponsePact shouldHandleMissingDateParameter(final PactDslWithProvider builder) {
    return builder.given("data count > 0")
                  .uponReceiving("a request with a missing date parameter")
                  .method("GET")
                  .path("/provider.json")
                  .willRespondWith()
                  .status(400)
                  .body("{\"error\": \"validDate is required\"}")
                  .headers(Collections.singletonMap("Content-Type", "application/json"))
                  .toPact();
  }

  @Pact(provider="ProviderMicroservice", consumer="ConsumerApp")
  public RequestResponsePact shouldHandleInvalidDateParameter(final PactDslWithProvider builder) {
    return builder.given("data count > 0")
            .uponReceiving("a request with a missing date parameter")
            .method("GET")
            .path("/provider.json")
             // NOTE: Need to be sure that you escape chars which are part of RegEx
            .matchQuery("validDate", "This is not a date")
            .willRespondWith()
            .status(400)
            .body("{\"error\": \"'This is not a date' is not a date\"}")
            .headers(Collections.singletonMap("Content-Type", "application/json"))
            .toPact();
  }

  @Pact(provider="ProviderMicroservice", consumer="ConsumerApp")
  public RequestResponsePact shouldHandleNoData(final PactDslWithProvider builder) throws IOException {
    final String dateString = date.atOffset(ZoneOffset.UTC).toString();
    return builder.given("data count == 0")
            .uponReceiving("a request for json data")
            .method("GET")
            .path("/provider.json")
            .matchQuery("validDate", dateString.replace(".", "\\."))
            .willRespondWith()
            .status(404)
            .toPact();
  }

  @Test
  @PactVerification(value = "ProviderMicroservice", fragment = "shouldHavePactWithOurProvider")
  public void runTestShouldHavePactWithOurProvider() throws Exception {
    final String dateString = "2013-08-16T15:31:20Z";
    final List<Object> output = client.fetchAndProcessData(dateString);
    assertThat(output, hasSize(2));
    assertThat(output.get(0), is(1));
    assertThat(output.get(1), is(equalTo(OffsetDateTime.parse("2013-08-16T15:31:20Z", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")))));
  }

  @Test
  @PactVerification(value = "ProviderMicroservice", fragment = "shouldHandleMissingDateParameter")
  public void runTestShouldHandleMissingDateParameterCheck() throws Exception {
    final List<Object> output = client.fetchAndProcessData(null);
    assertThat(output, hasSize(2));
    assertThat(output.get(0), is(0));
    assertThat(output.get(1), is((Object)null));
  }

  @Test
  @PactVerification(value = "ProviderMicroservice", fragment = "shouldHandleInvalidDateParameter")
  public void runTestShouldHandleInvalidDateParameterCheck() throws Exception {
    final List<Object> output = client.fetchAndProcessData("This is not a date");
    assertThat(output, hasSize(2));
  }

  @Test
  @PactVerification(value = "ProviderMicroservice", fragment = "shouldHandleNoData")
  public void runTestShouldHandleNoDataCheck() throws Exception {
    final String dateString = date.atOffset(ZoneOffset.UTC).toString();
    final List<Object> output = client.fetchAndProcessData(dateString);
    assertThat(output, hasSize(2));
  }

}
