/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.wasm.api;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.ExportLibrary;
import org.graalvm.wasm.exception.WasmJsApiException;

@ExportLibrary(InteropLibrary.class)
public class Global extends Dictionary {
    private final Object descriptor;
    private final ValueType valueType;
    private final boolean mutable;
    private Object value;

    public Global(Object descriptor, Object value) {
        this.descriptor = descriptor;
        try {
            this.valueType = ValueType.valueOf((String) InteropLibrary.getUncached().readMember(descriptor, "value"));
            this.mutable = (boolean) InteropLibrary.getUncached().readMember(descriptor, "mutable");
        } catch (UnsupportedMessageException | UnknownIdentifierException e) {
            throw new WasmJsApiException(WasmJsApiException.Kind.TypeError, "Invalid global descriptor: " + descriptor);
        }
        setInternal(value);
        addMembers(new Object[]{
                        "descriptor", this.descriptor,
                        "valueOf", new Executable(args -> get()),
                        "value", value,
        });
    }

    public Object get() {
        return value;
    }

    public ValueType valueType() {
        return valueType;
    }

    public boolean mutable() {
        return mutable;
    }

    public void set(Object value) {
        if (!mutable) {
            throw new WasmJsApiException(WasmJsApiException.Kind.TypeError, "Global is not mutable.");
        }
        setInternal(value);
    }

    private void setInternal(Object value) {
        switch (valueType) {
            case i32:
                if (!(value instanceof Integer)) {
                    throw new WasmJsApiException(WasmJsApiException.Kind.TypeError, "Global type " + valueType + ", value: " + value);
                }
                this.value = value;
                break;
            case i64:
                if (!(value instanceof Long)) {
                    throw new WasmJsApiException(WasmJsApiException.Kind.TypeError, "Global type " + valueType + ", value: " + value);
                }
                this.value = value;
                break;
            case f32:
                if (!(value instanceof Float)) {
                    throw new WasmJsApiException(WasmJsApiException.Kind.TypeError, "Global type " + valueType + ", value: " + value);
                }
                this.value = value;
                break;
            case f64:
                if (!(value instanceof Double)) {
                    throw new WasmJsApiException(WasmJsApiException.Kind.TypeError, "Global type " + valueType + ", value: " + value);
                }
                this.value = value;
                break;
        }
    }
}
