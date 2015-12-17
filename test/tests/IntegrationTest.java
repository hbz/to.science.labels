package tests;

import org.junit.*;

import play.test.*;
import play.libs.F.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

@SuppressWarnings("javadoc")
public class IntegrationTest {
    /**
     * This integration test uses Solenium to test the app with a browser
     */
    // @Test
    public void test() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/tools/etikett");
                assertThat(browser.pageSource()).contains("Application Profile");
            }
        });
    }
}
