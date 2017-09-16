package dev.wolveringer.config.test;

import dev.wolveringer.config.annotation.CommentGenerator;
import dev.wolveringer.config.annotation.Comments;
import dev.wolveringer.config.annotation.NonNull;
import dev.wolveringer.config.annotation.Path;
import dev.wolveringer.config.annotation.defaults.EnumAvariableListener;
import dev.wolveringer.config.exception.ConfigException;
import dev.wolveringer.config.exception.InvalidConfigException;
import dev.wolveringer.config.yaml.YamlCommentHelper;
import dev.wolveringer.config.yaml.YamlConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wolverindev on 09.09.17.
 */
public class YamlParseTest {
    public static enum TestEnum {
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I
    }

    @Getter
    @ToString
    @Comments("Hello")
    public static class TestYamlConfig extends YamlConfig {
        public TestYamlConfig() {
            super(new File("src/test/resources/test.yml"));
        }

        @Path("test.entry")
        @Comments({"Hello", "World1"})
        @dev.wolveringer.config.annotation.NonNull
        private TestYamlValue testEntry1 = new TestYamlValue("This is a test entry!");

        @Path("test.entry2")
        @Comments({"Hello", "World2"})
        private TestYamlValue testEntry2 = new TestYamlValue("This is the second test entry!");

        @Path("test.enum")
        //@CommentGenerator(generatorClass = YamlParseTest.class, methode = "generateTestComment")
        @EnumAvariableListener
        @NonNull
        private TestEnum testEnum = TestEnum.A;

        @EnumAvariableListener(targetClass = TestEnum.class)
        private TestEnum[] testEnumArray = {TestEnum.A, TestEnum.C};

        @NonNull
        @Getter
        private List<TestYamlValue> values = new ArrayList<>(Arrays.asList(new TestYamlValue("First list entry"), new TestYamlValue("Second list entzry!")));

        @Override
        protected boolean checkConfig() throws ConfigException {
            //if(testEnum == TestEnum.A) throw new InvalidConfigException(testEnum + " == " + TestEnum.A);
            //return testEnum == TestEnum.B;
            return true;
        }
    }

    @Getter
    @ToString
    @Comments("XXYYY")
    public static class TestYamlValue extends YamlConfig {
        @Comments("Im a test string!")
        private String ztestString = "Test string";

        @Path("xxxy")
        private String value;

        @Comments("Im a Xtest string!")
        private String xtestString = "Test string";

        public TestYamlValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testParseTestYaml() throws ConfigException {
        TestYamlConfig cfg = new TestYamlConfig();
        cfg.values.clear();
        cfg.load();
        System.out.println(cfg);
    }

    @Test
    public void testSaveTestYaml() throws ConfigException {
        TestYamlConfig cfg = new TestYamlConfig();
        cfg.save();
        System.out.println(cfg);
    }

    public static String generateTestComment() {
        System.out.println("Generate!");
        return "Avariable stuff: " + YamlCommentHelper.join(Arrays.asList(TestEnum.values()).stream().map(e -> e.name()).collect(Collectors.toList()), ", ");
    }
}
