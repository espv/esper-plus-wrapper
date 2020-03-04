/*******************************************************************************
 * Copyright (c) 2015, 2016 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

/**
 * ATTRIBUTION NOTICE
 * ==================
 * MinimalJson is a fast and minimal JSON parser and writer for Java. It's not an object mapper, but a bare-bones library that aims at being:
 * - minimal: no dependencies, single package with just a few classes, small download size (< 35kB)
 * - fast: high performance comparable with other state-of-the-art parsers (see below)
 * - lightweight: object representation with minimal memory footprint (e.g. no HashMaps)
 * - simple: reading, writing and modifying JSON with minimal code (short names, fluent style)
 *
 * Minimal JSON can be found at https://github.com/ralfstx/minimal-json.
 *
 * Minimal JSON is licensed under the MIT License, see https://github.com/ralfstx/minimal-json/blob/master/LICENSE
 *
 */

package com.espertech.esper.common.client.json.minimaljson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;


public class ParseException_Test {

  private Location location;

  @Before
  public void setUp() {
    location = new Location(4711, 23, 42);
  }

  @Test
  public void location() {
    ParseException exception = new ParseException("Foo", location);

    assertSame(location, exception.getLocation());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void position() {
    ParseException exception = new ParseException("Foo", location);

    assertEquals(location.offset, exception.getOffset());
    assertEquals(location.line, exception.getLine());
    assertEquals(location.column, exception.getColumn());
  }

  @Test
  public void message() {
    ParseException exception = new ParseException("Foo", location);

    assertEquals("Foo at 23:42", exception.getMessage());
  }

}
