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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;


/**
 * Enables human readable JSON output by inserting whitespace between values.after commas and
 * colons. Example:
 *
 * <pre>
 * jsonValue.writeTo(writer, PrettyPrint.singleLine());
 * </pre>
 */
public class PrettyPrint extends WriterConfig {

    private final char[] indentChars;

    protected PrettyPrint(char[] indentChars) {
        this.indentChars = indentChars;
    }

    /**
     * Print every value on a separate line. Use tabs (<code>\t</code>) for indentation.
     *
     * @return A PrettyPrint instance for wrapped mode with tab indentation
     */
    public static PrettyPrint singleLine() {
        return new PrettyPrint(null);
    }

    /**
     * Print every value on a separate line. Use the given number of spaces for indentation.
     *
     * @param number the number of spaces to use
     * @return A PrettyPrint instance for wrapped mode with spaces indentation
     */
    public static PrettyPrint indentWithSpaces(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("number is negative");
        }
        char[] chars = new char[number];
        Arrays.fill(chars, ' ');
        return new PrettyPrint(chars);
    }

    /**
     * Do not break lines, but still insert whitespace between values.
     *
     * @return A PrettyPrint instance for single-line mode
     */
    public static PrettyPrint indentWithTabs() {
        return new PrettyPrint(new char[]{'\t'});
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return new PrettyPrintWriter(writer, indentChars);
    }

    private static class PrettyPrintWriter extends JsonWriter {

        private final char[] indentChars;
        private int indent;

        private PrettyPrintWriter(Writer writer, char[] indentChars) {
            super(writer);
            this.indentChars = indentChars;
        }

        @Override
        public void writeArrayOpen() throws IOException {
            indent++;
            writer.write('[');
            writeNewLine();
        }

        @Override
        public void writeArrayClose() throws IOException {
            indent--;
            writeNewLine();
            writer.write(']');
        }

        @Override
        public void writeArraySeparator() throws IOException {
            writer.write(',');
            if (!writeNewLine()) {
                writer.write(' ');
            }
        }

        @Override
        public void writeObjectOpen() throws IOException {
            indent++;
            writer.write('{');
            writeNewLine();
        }

        @Override
        public void writeObjectClose() throws IOException {
            indent--;
            writeNewLine();
            writer.write('}');
        }

        @Override
        public void writeMemberSeparator() throws IOException {
            writer.write(':');
            writer.write(' ');
        }

        @Override
        public void writeObjectSeparator() throws IOException {
            writer.write(',');
            if (!writeNewLine()) {
                writer.write(' ');
            }
        }

        private boolean writeNewLine() throws IOException {
            if (indentChars == null) {
                return false;
            }
            writer.write('\n');
            for (int i = 0; i < indent; i++) {
                writer.write(indentChars);
            }
            return true;
        }

    }

}
