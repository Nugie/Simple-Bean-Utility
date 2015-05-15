/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    /**
     *
     * @param clz
     */
    public BeanDescriptor(Class<T> clz) {
        this.hashCode = clz.hashCode();
        this.classHandleName = ("com.baculsoft.beanutils.gen.$$" + clz.getSimpleName() + "Desc\nhashcode:" + hashCode);
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
     * @return
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
     * @return
     */
    public abstract String toString(T target);

    /**
     *
     * @param objectOutput
     * @param target
     */
    public abstract void serialize(ObjectOutputStream objectOutput, T target);

    
    public abstract T newInstance();
    
    /**
     *
     * @param b
     * @return
     */
    protected static Boolean autoboxing(boolean b) {
        return b;
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
