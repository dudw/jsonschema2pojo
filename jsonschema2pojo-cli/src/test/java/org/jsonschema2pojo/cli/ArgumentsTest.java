/**
 * Copyright © 2010-2020 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsonschema2pojo.cli;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.jsonschema2pojo.InclusionLevel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArgumentsTest {

    private static final PrintStream SYSTEM_OUT = System.out;
    private static final PrintStream SYSTEM_ERR = System.err;
    private final ByteArrayOutputStream systemOutCapture = new ByteArrayOutputStream();
    private final ByteArrayOutputStream systemErrCapture = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(systemOutCapture));
        System.setErr(new PrintStream(systemErrCapture));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(SYSTEM_OUT);
        System.setErr(SYSTEM_ERR);
    }

    @Test
    public void parseRecognisesValidArguments() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {
                "--source", "/home/source", "--target", "/home/target", "--disable-getters", "--package", "mypackage",
                "--generate-builders", "--use-primitives", "--omit-hashcode-and-equals", "--omit-tostring", "--include-dynamic-accessors",
                "--include-dynamic-getters", "--include-dynamic-setters", "--include-dynamic-builders", "--inclusion-level", "ALWAYS"
        });

        assertThat(args.didExit(), is(false));
        assertThat(args.getSource().next().getFile(), endsWith("/home/source"));
        assertThat(args.getTargetDirectory(), is(theFile("/home/target")));
        assertThat(args.getTargetPackage(), is("mypackage"));
        assertThat(args.isGenerateBuilders(), is(true));
        assertThat(args.isUsePrimitives(), is(true));
        assertThat(args.isIncludeHashcodeAndEquals(), is(false));
        assertThat(args.isIncludeToString(), is(false));
        assertThat(args.isIncludeGetters(), is(false));
        assertThat(args.isIncludeSetters(), is(true));
        assertThat(args.isIncludeDynamicAccessors(), is(true));
        assertThat(args.isIncludeDynamicGetters(), is(true));
        assertThat(args.isIncludeDynamicSetters(), is(true));
        assertThat(args.isIncludeDynamicBuilders(), is(true));
        assertThat(args.getInclusionLevel(), is(InclusionLevel.ALWAYS));
    }

    @Test
    public void parseRecognisesShorthandArguments() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {
                "-s", "/home/source", "-t", "/home/target", "-p", "mypackage", "-b", "-P", "-E", "-S", "-ida", "-idg", "-ids", "-idb", "-il", "ALWAYS"
        });

        assertThat(args.didExit(), is(false));
        assertThat(args.getSource().next().getFile(), endsWith("/home/source"));
        assertThat(args.getTargetDirectory(), is(theFile("/home/target")));
        assertThat(args.getTargetPackage(), is("mypackage"));
        assertThat(args.isGenerateBuilders(), is(true));
        assertThat(args.isUsePrimitives(), is(true));
        assertThat(args.isIncludeHashcodeAndEquals(), is(false));
        assertThat(args.isIncludeToString(), is(false));
        assertThat(args.isIncludeDynamicAccessors(), is(true));
        assertThat(args.isIncludeDynamicGetters(), is(true));
        assertThat(args.isIncludeDynamicSetters(), is(true));
        assertThat(args.isIncludeDynamicBuilders(), is(true));
        assertThat(args.getInclusionLevel(), is(InclusionLevel.ALWAYS));
    }

    @Test
    public void parserAcceptsHyphenWordDelimiter() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {
                "-s", "/home/source", "-t", "/home/target", "--word-delimiters", "-"
        });

        assertThat(args.getPropertyWordDelimiters(), is(new char[] { '-' }));
    }

    @Test
    public void allOptionalArgsCanBeOmittedAndDefaultsPrevail() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {
                "--source", "/home/source", "--target", "/home/target"
        });

        assertThat(args.didExit(), is(false));
        assertThat(args.getSource().next().getFile(), endsWith("/home/source"));
        assertThat(args.getTargetDirectory(), is(theFile("/home/target")));
        assertThat(args.getTargetPackage(), is(nullValue()));
        assertThat(args.isGenerateBuilders(), is(false));
        assertThat(args.isUsePrimitives(), is(false));
        assertThat(args.isIncludeHashcodeAndEquals(), is(true));
        assertThat(args.isIncludeToString(), is(true));
        assertThat(args.isIncludeGetters(), is(true));
        assertThat(args.isIncludeSetters(), is(true));
        assertThat(args.isIncludeDynamicAccessors(), is(false));
        assertThat(args.isIncludeDynamicGetters(), is(false));
        assertThat(args.isIncludeDynamicSetters(), is(false));
        assertThat(args.isIncludeDynamicBuilders(), is(false));
    }

    @Test
    public void missingArgsCausesHelp() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {});

        assertThat(args.status, is(1));
        assertThat(new String(systemErrCapture.toByteArray(), StandardCharsets.UTF_8), is(containsString("--target")));
        assertThat(new String(systemErrCapture.toByteArray(), StandardCharsets.UTF_8), is(containsString("--source")));
        assertThat(new String(systemOutCapture.toByteArray(), StandardCharsets.UTF_8), is(containsString("Usage: jsonschema2pojo")));
    }

    @Test
    public void requestingHelpCausesHelp() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] { "--help" });

        assertThat(args.status, is(notNullValue()));
        assertThat(new String(systemOutCapture.toByteArray(), StandardCharsets.UTF_8), is(containsString("Usage: jsonschema2pojo")));
    }

    @Test
    public void requestingVersionCausesVersion() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] { "--version" });

        assertThat(args.didExit(), is(true));
        assertThat(new String(systemOutCapture.toByteArray(), StandardCharsets.UTF_8).matches("(?s)jsonschema2pojo version \\d.*"), is(true));
    }

    @Test
    public void parseRecognisesSourceWithMultipleValues() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {
                "-s", "/home/source", "/home/second_source", "-t", "/home/target"
        });

        assertThat(args.didExit(), is(false));
        final Iterator<URL> sources = args.getSource();
        assertThat(sources.next().getFile(), endsWith("/home/source"));
        assertThat(sources.next().getFile(), endsWith("/home/second_source"));
        assertThat(sources.hasNext(), is(false));
    }

    @Test
    public void parseRecognisesMultipleSources() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {
                "-s", "/home/source", "-s", "/home/second_source", "-t", "/home/target"
        });

        assertThat(args.didExit(), is(false));
        final Iterator<URL> sources = args.getSource();
        assertThat(sources.next().getFile(), endsWith("/home/source"));
        assertThat(sources.next().getFile(), endsWith("/home/second_source"));
        assertThat(sources.hasNext(), is(false));
    }

    @Test
    public void parseRecognisesMultipleSourcesWithMultipleValues() {
        ArgsForTest args = (ArgsForTest) new ArgsForTest().parse(new String[] {
                "-s", "/home/source", "/home/second_source", "-s", "/home/third_source", "-t", "/home/target"
        });

        assertThat(args.didExit(), is(false));
        final Iterator<URL> sources = args.getSource();
        assertThat(sources.next().getFile(), endsWith("/home/source"));
        assertThat(sources.next().getFile(), endsWith("/home/second_source"));
        assertThat(sources.next().getFile(), endsWith("/home/third_source"));
        assertThat(sources.hasNext(), is(false));
    }

    private File theFile(String path) {
        return new File(path);
    }

    private static class ArgsForTest extends Arguments {
        protected Integer status;

        @Override
        protected void exit(int status) {
            this.status = status;
        }

        protected boolean didExit() {
            return (status != null);
        }
    }
}
