package tests;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import models.Etikett;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import controllers.MyController;
import helper.EtikettMaker;

/**
 *
 * Simple (JUnit) tests that can call all parts of a Play app. If you are
 * interested in mocking a whole application, see the wiki for more details.
 *
 */
@SuppressWarnings("javadoc")
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testContextCreation() {
        running(fakeApplication(play.test.Helpers.inMemoryDatabase()), () -> {
            try {
                ObjectMapper mapper = MyController.getMapper();
                mapper.setSerializationInclusion(Include.NON_EMPTY);
                EtikettMaker profile = new EtikettMaker();
                profile.addJsonData((List<Etikett>) mapper.readValue(new FileInputStream("test/resources/labels.json"),
                        new TypeReference<List<Etikett>>() {
                }));
                Map<String, Object> actual = EtikettMaker.getContext();
                Map<String, Object> expected = mapper.setSerializationInclusion(Include.NON_NULL)
                        .readValue(new File("test/resources/context.json"), Map.class);
                JsonNode actNode = mapper.convertValue(actual, JsonNode.class);
                JsonNode expectNode = mapper.convertValue(expected, JsonNode.class);
                Files.write(actNode.toString(), new File("/tmp/etikett-test.log"), Charsets.UTF_8);
                boolean result = new CompareJsonMaps().compare(actNode, expectNode);
                org.junit.Assert.assertTrue(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

}
