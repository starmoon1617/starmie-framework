/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.generator.core.api;

import java.util.List;
import java.util.Map;

import io.github.starmoon1617.starmie.generator.core.data.FieldData;

/**
 * Interface to generate field for class
 * 
 * @date 2023-10-27
 * @author Nathan Liao
 */
public interface FieldGenerator extends DataGenerator {

    List<FieldData> generate(Map<String, Object> attrs);
}
