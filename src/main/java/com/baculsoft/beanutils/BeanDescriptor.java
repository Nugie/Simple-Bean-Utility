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

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Natalino Nugeraha
 * @param <T>
 */
public abstract class BeanDescriptor<T> implements Serializable {

    private static final long serialVersionUID = -7869299109566689806L;

    private final int hashCode;

    private final String classHandleName;

    protected static final boolean BOOLEAN_DEFAULT=false;
    protected static final char CHAR_DEFAULT=0;
    protected static final byte BYTE_DEFAULT=0;
    protected static final short SHORT_DEFAULT=0;
    protected static final int INT_DEFAULT=0;
    protected static final long LONG_DEFAULT=0;
    
    protected static final float FLOAT_DEFAULT=0f;
    protected static final double DOUBLE_DEFAULT=0d;

    /**
     *
     * @param clz
     * @exception NullPointerException if clz parameter is null
     */
    @SuppressWarnings("Unchecked")
    public BeanDescriptor(Class<T> clz) {
        this.hashCode = clz.hashCode();
        this.classHandleName = (clz.getPackage().getName()+".$$" + clz.getSimpleName() + "Desc\nhashcode:" + hashCode);
    }

    /**
     *
     * @param mapSource
     * @param target
     */
    public abstract void copyFromMap(Map<String, ?> mapSource, T target);

    /**
     *
     * @param source
     * @param target
     */
    public abstract void copy(T source, T target);

    /**
     * 
     * @param source
     * @param target 
     */
    public abstract void copyPropertyWhenNotNull(T source, T target);
    
    /**
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public abstract void copy(T source, T target, String[] ignoreProperties);

    /**
     *
     * @param target
     * @return
     */
    public abstract Map<String, ?> describe(T target);

    /**
     *
     * @param target
     * @return
     */
    public abstract Map<String, ?> describeNotNullProperty(T target);

    /**
     *
     * @param <R>
     * @param propertyName
     * @param target
     * @return null if propertyName not found or target is null
     */
    public abstract <R> R invokeGetter(String propertyName, T target);

    /**
     *
     * @param propertyName
     * @param target
     * @param value
     */
    public abstract void invokeSetter(String propertyName, T target, Object value);

    /**
     *
     * @param target
     * @return String all property value 
     */
    public abstract String toString(T target);

    /**
     *
     * @param objectOutputStream
     * @param target
     */
    public abstract void serialize(ObjectOutputStream objectOutputStream, T target);

    /**
     * 
     * @param target
     * @return 
     */
    public abstract byte[] serialize(T target);
    
    /**
     * 
     * @return new instance or null if no constructor not found
     */
    public abstract T newInstance();

    /**
     * 
     * @param obj1
     * @param obj2
     * @return 
     */
    public abstract T compare(T obj1,T obj2);

    /**
     * 
     * @param obj 
     */
    public abstract void reset(T obj);

    
    /**
     *
     * @param b
     * @return
     */
    protected static Boolean autoboxing(boolean b) {
        return b?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     *
     * @param c
     * @return
     */
    protected static Character autoboxing(char c) {
        return c;
    }

    /**
     *
     * @param b
     * @return
     */
    protected static Byte autoboxing(byte b) {
        return b;
    }

    /**
     *
     * @param s
     * @return
     */
    protected static Short autoboxing(short s) {
        return s;
    }

    /**
     *
     * @param i
     * @return
     */
    protected static Integer autoboxing(int i) {
        return i;
    }

    /**
     *
     * @param l
     * @return
     */
    protected static Long autoboxing(long l) {
        return l;
    }

    /**
     *
     * @param f
     * @return
     */
    protected static Float autoboxing(float f) {
        return f;
    }

    /**
     *
     * @param d
     * @return
     */
    protected static Double autoboxing(double d) {
        return d;
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }

    @Override
    public final boolean equals(Object obj) {
        return (obj instanceof BeanDescriptor) && this.hashCode != ((BeanDescriptor<?>) obj).hashCode;
    }

    @Override
    public String toString() {
        return classHandleName;
    }

}
