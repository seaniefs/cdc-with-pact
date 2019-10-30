package au.com.dius.pactworkshop.springbootprovider;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.VerificationReports;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRestPactRunner.class)
@Provider("ProviderMicroservice")
@PactBroker(scheme = "${pactbroker.scheme}", host = "${pactbroker.host}", port = "${pactbroker.port}", authentication =
    @PactBrokerAuth(username = "${pactbroker.auth.username}", password = "${pactbroker.auth.password}")
)
@VerificationReports("console")
@SpringBootTest(classes = MainApplication.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class HttpContractTest {

    @TestTarget
    public Target target;

    @LocalServerPort
    public void init(int port) {
        target = new HttpTarget("http", "localhost", port, "/", true);
    }

    @State("data count > 0")
    public void toDefaultState() {
        DataStore.INSTANCE.setDataCount(100);
        System.out.println("Now service in default state");
    }
    @State("data count == 0")
    public void toEmptyState() {
        DataStore.INSTANCE.setDataCount(0);
        System.out.println("Now service in empty state");
    }
    /* Can also accept parameters...
    fun `book with fixed ID exists`(params: Map<String, String>) {
        val bookId = BookId.from(params["bookId"]!!)
        val bookRecord = BookRecord(bookId, Books.THE_MARTIAN)
        given { dataStore.findById(bookId) }.willReturn { bookRecord }
        given { dataStore.createOrUpdate(any()) }.willAnswer { it.arguments[0] as BookRecord }
    }
    */

}