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

package org.jsonschema2pojo.integration.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.jsonschema2pojo.integration.util.CodeGenerationHelper.*;

import java.io.Serializable;

import org.junit.jupiter.api.Test;

public class SerializableIT {

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void beansDoNotIncludeSerializableByDefault() throws ClassNotFoundException, SecurityException {

        ClassLoader resultsClassLoader = generateAndCompile("/schema/properties/primitiveProperties.json", "com.example");

        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");

        assertThat("Beans should not implement serializable by default", generatedType, is(instanceOf(Serializable.class)));
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void beansIncludeSerializableWhenConfigIsSet() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", config("serializable", true));

        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");

        assertThat("Beans should implement serializable when config is set", generatedType, is(instanceOf(Serializable.class)));
    }

    @Test
    @SuppressWarnings({ "rawtypes" })
    public void beansCanIncludeConstructor() throws ClassNotFoundException, SecurityException {
        ClassLoader resultsClassLoader = generateAndCompile("/schema/properties/primitiveProperties.json", "com.example", config("serializable", true, "includeConstructors", true));

        Class generatedType = resultsClassLoader.loadClass("com.example.PrimitiveProperties");

        assertThat("Beans should implement serializable when config is set", generatedType, is(instanceOf(Serializable.class)));
    }

}
