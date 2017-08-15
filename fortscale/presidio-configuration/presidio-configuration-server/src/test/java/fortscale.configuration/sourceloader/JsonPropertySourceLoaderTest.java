package fortscale.configuration.sourceloader;

import org.junit.Test;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonPropertySourceLoaderTest {

    private JsonPropertySourceLoader loader = new JsonPropertySourceLoader();

    @Test
    public void load() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("{\"foo\": \"bar\"}".getBytes());
        PropertySource<?> source = this.loader.load("resource", resource, null);
        assertThat(source).isNotNull();
        assertThat(source.getProperty("foo")).isEqualTo("bar");
    }

    @Test
    public void nestedItems() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("{\"foo\": { \"bar\": \"spam\" }}".getBytes());
        PropertySource<?> source = this.loader.load("resource", resource, null);
        assertThat(source).isNotNull();
        assertThat(source.getProperty("foo.bar")).isEqualTo("spam");
    }

    @Test
    public void arrayItems() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("{\"foo\": [\"bar\",\"baz\"]}".getBytes());
        PropertySource<?> source = this.loader.load("resource", resource, null);
        assertThat(source).isNotNull();
        System.out.println(source.getProperty("foo"));
        ArrayList<String> expected = new ArrayList<String>(Arrays.asList( new String[] {"bar","baz"}));
        assertThat(source.getProperty("foo")).isEqualTo(expected);
    }

    @Test
    public void orderedItems() throws Exception {
        StringBuilder json = new StringBuilder();
        json.append("{");
        List<String> expected = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            json.append(String.format("\"%c\":\"value%c\"", c, c));
            if (c < 'z') {
                json.append(",");
            }
            json.append("\n");
            expected.add(String.valueOf(c));
        }
        json.append("}");
        ByteArrayResource resource = new ByteArrayResource(json.toString().getBytes());
        EnumerablePropertySource<?> source = (EnumerablePropertySource<?>) this.loader.load("resource", resource, null);
        assertThat(source).isNotNull();
        assertThat(source.getPropertyNames()).isEqualTo(expected.toArray(new String[] {}));
    }

    @Test
    public void invalidJson() throws Exception {
        ByteArrayResource resource = new ByteArrayResource("{\"foo\": \"bar\"".getBytes());
        PropertySource<?> source = this.loader.load("resource", resource, null);
        assertThat(source).isNotNull();
        assertThat(source.getProperty("foo")).isEqualTo("bar");
    }

}
