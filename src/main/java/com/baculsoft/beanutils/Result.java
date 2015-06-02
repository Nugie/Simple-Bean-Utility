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

import java.io.Serializable;

/**
 *
 * @author Natalino Nugeraha
 */
public final class Result<V> implements Serializable{

    private final String name;
    private final V value1;
    private final V value2;
    private final int hashCode;

    /**
     *
     * @param name
     * @param value1
     * @param value2
     */
    public Result(String name, V value1, V value2) {
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.hashCode = (this.name != null ? this.name.hashCode() : 0)+ (this.value1 != null ? this.value1.hashCode() : 0)+ (this.value2 != null ? this.value2.hashCode() : 0);
    }


    /**
     * 
     * @return name
     */
    public String getName() {
        return name;
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

    /**
     * 
     * @return hashCode
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Result) {
            final Result<?> other = (Result<?>) obj;
            if (! (name == other.name) || (name != null && name.equals(other.name))) {
                return false;
            }
            else if (! (value1 == other.value1) || (value1 != null && value1.equals(other.value1))) {
                return false;
            }
            else if (! (value2 == other.value2) || (value2 != null && value2.equals(other.value2))) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 
     * @return toString
     */
    @Override
    public String toString() {
        return "Result{" + "name=" + name + ", value1=" + value1 + ", value2=" + value2 + '}';
    }

}
