package tests;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import controllers.Globals;
import controllers.MyController;
import helper.EtikettMaker;
import models.Etikett;

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
                Map<String, Object> actual = profile.getContext();
                Map<String, Object> expected = mapper.setSerializationInclusion(Include.NON_NULL)
                        .readValue(new File("test/resources/context.json"), Map.class);
                JsonNode actNode = mapper.convertValue(actual, JsonNode.class);
                JsonNode expectNode = mapper.convertValue(expected, JsonNode.class);
                Files.write(actNode.toString(), new File("/tmp/etikett-test.json"), Charsets.UTF_8);

                Files.write(expectNode.toString(), new File("/tmp/etikett-test-expected.json"), Charsets.UTF_8);
                boolean result = new CompareJsonMaps().compare(actNode, expectNode);
                org.junit.Assert.assertTrue(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Test
    public void testDefaultResolver() {
        running(fakeApplication(play.test.Helpers.inMemoryDatabase()), () -> {
            String label = EtikettMaker.lookUpLabel("http://aims.fao.org/aos/agrovoc/c_92373", "en");
            Assert.assertTrue("Mississippi River".equals(label));
            label = EtikettMaker.lookUpLabel("http://aims.fao.org/aos/agrovoc/c_92373", "de");
            Assert.assertTrue("Mississippi <fluss>".equals(label));
            label = EtikettMaker.lookUpLabel("http://d-nb.info/gnd/141568992");
            Assert.assertTrue("Twain, Mark".equals(label));
        });
    }

    @Test
    public void testOnTheFlySkosConversion_de() {
        running(fakeApplication(play.test.Helpers.inMemoryDatabase()), () -> {
            try (InputStream in = new FileInputStream("test/resources/107.ttl")) {
                List<Etikett> result = Globals.profile.convertRdfData(in, "de");
                String expected = "[{\"uri\":\"http://aims.fao.org/aos/agrovoc/c_10175\",\"label\":\"Erntegutlagerung\",\"multilangLabel\":{\"de\":\"Erntegutlagerung\",\"hi\":\"फसल भण्डारण\",\"ru\":\"хранение урожая\",\"lo\":\"ການເກັບຮັກສາພືດຜົນ\",\"pt\":\"Armazenamento das colheitas\",\"ko\":\"작물저장\",\"en\":\"crop storage\",\"it\":\"Immagazzinamento dei raccolti\",\"fr\":\"Stockage des récoltes\",\"hu\":\"termés raktározása\",\"zh\":\"收获储藏\",\"cs\":\"skladování plodin\",\"th\":\"การเก็บรักษาพืชผล\",\"ja\":\"作物貯蔵\",\"sk\":\"skladovanie plodín\",\"fa\":\"ذخيره‌سازي محصولات زراعی\",\"pl\":\"Magazynowanie ziemiopłodów\",\"tr\":\"ürün depolama\"}}]";
                play.Logger.debug(MyController.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));
                Assert.assertTrue(expected.equals(MyController.getMapper().writeValueAsString(result)));
            } catch (Exception e) {
                play.Logger.warn("", e);
            }
        });
    }

    @Test
    public void testOnTheFlySkosConversion_en() {
        running(fakeApplication(play.test.Helpers.inMemoryDatabase()), () -> {
            try (InputStream in = new FileInputStream("test/resources/107.ttl")) {
                List<Etikett> result = Globals.profile.convertRdfData(in, "en");
                String expected = "[{\"uri\":\"http://aims.fao.org/aos/agrovoc/c_10175\",\"label\":\"crop storage\",\"multilangLabel\":{\"de\":\"Erntegutlagerung\",\"hi\":\"फसल भण्डारण\",\"ru\":\"хранение урожая\",\"lo\":\"ການເກັບຮັກສາພືດຜົນ\",\"pt\":\"Armazenamento das colheitas\",\"ko\":\"작물저장\",\"en\":\"crop storage\",\"it\":\"Immagazzinamento dei raccolti\",\"fr\":\"Stockage des récoltes\",\"hu\":\"termés raktározása\",\"zh\":\"收获储藏\",\"cs\":\"skladování plodin\",\"th\":\"การเก็บรักษาพืชผล\",\"ja\":\"作物貯蔵\",\"sk\":\"skladovanie plodín\",\"fa\":\"ذخيره‌سازي محصولات زراعی\",\"pl\":\"Magazynowanie ziemiopłodów\",\"tr\":\"ürün depolama\"}}]";
                play.Logger.debug(MyController.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));
                Assert.assertTrue(expected.equals(MyController.getMapper().writeValueAsString(result)));
            } catch (Exception e) {
                play.Logger.warn("", e);
            }
        });
    }

    @Test
    public void testOnTheFlyJsonContextConversion() {
        running(fakeApplication(play.test.Helpers.inMemoryDatabase()), () -> {
            try (InputStream in = new FileInputStream("test/resources/context.json")) {
                @SuppressWarnings("unchecked")
                List<Etikett> result = Globals.profile
                        .convertJsonContextData((Map<String, Object>) new ObjectMapper().readValue(in, Map.class));
                play.Logger.debug(MyController.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));
            } catch (Exception e) {
                play.Logger.warn("", e);
            }
        });
    }

    @Test
    public void testOnTheFlyJsonConversion() {
        running(fakeApplication(play.test.Helpers.inMemoryDatabase()), () -> {
            try (InputStream in = new FileInputStream("test/resources/labels.json")) {
                @SuppressWarnings("unchecked")
                List<Etikett> result = ((List<Etikett>) new ObjectMapper().readValue(in,
                        new TypeReference<List<Etikett>>() {
                        }));
                play.Logger.debug(MyController.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));
            } catch (Exception e) {
                play.Logger.warn("", e);
            }
        });
    }

}
