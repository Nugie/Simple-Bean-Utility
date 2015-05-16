/*
 * Copyright 2015 baculsoft.com.
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
package com.baculsoft.beanutils;

/**
 *
 * @author Natalino Nugeraha
 */
public final class Result<V> {
    private final V value1;
    private final V value2;

    Result(V value1,V value2) {
        this.value1=value1;
        this.value2=value2;
    }

    /**
     * 
     * @return value1
     */
    public V getValue1() {
        return value1;
    }

    /**
     * 
     * @return value2
     */
    public V getValue2() {
        return value2;
    }
}
