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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 *
 * @author Natalino Nugeraha
 */
public final class BeanUtility {

    private static final Map<Class, BeanDescriptor> mapBeanDescriptor = new ConcurrentHashMap<Class, BeanDescriptor>(16, 0.75f, 64);
    private static final ClassPool classPool = ClassPool.getDefault();
    private static CtClass clParent;

    private static final String PREFIX_CLASS_NAME_GEN = "com.baculsoft.beanutils.gen.$$";
    private static final String SUFFIX_CLASS_NAME_GET = "Desc";
    private static final String PREFIX_GETTER = "get";
    private static final String PREFIX_IS = "is";
    private static final String PREFIX_SETTER = "set";
    private static final int CLASS_MODIFIER = Modifier.FINAL | Modifier.PUBLIC;

    private static final char[] HEADER_METHOD_INVOKE_GETTER = "public final Object invokeGetter(String propertyName,Object obj){".toCharArray();
    private static final char[] HEADER_METHOD_INVOKE_SETTER = "public final void invokeSetter(String propertyName,Object obj, Object value){".toCharArray();
    private static final char[] HEADER_METHOD_COPY_IGNORE_PROPERTIES = "public final void copy(Object objSource,Object objTarget,String[] ignoreProperties){".toCharArray();
    private static final char[] HEADER_METHOD_COPY = "public final void copy(Object objSource,Object objTarget){".toCharArray();
    private static final char[] HEADER_METHOD_COPY_PROPERTY_WHEN_NOT_NULL = "public final void copyPropertyWhenNotNull(Object objSource,Object objTarget){".toCharArray();
    private static final char[] HEADER_METHOD_COPY_FROM_MAP = "public final void copyFromMap(java.util.Map c,Object objTarget){".toCharArray();
    private static final char[] HEADER_METHOD_DESCRIBE_NOT_NULL = "public final java.util.Map describeNotNullProperty(Object obj){".toCharArray();
    private static final char[] HEADER_METHOD_DESCRIBE = "public final java.util.Map describe(Object obj){".toCharArray();
    private static final char[] HEAEDER_METHOD_TO_STRING = "public final String toString(Object obj){".toCharArray();
    private static final char[] HEADER_METHOD_SERIALIZE = "public final void serialize(java.io.ObjectOutputStream objectOutput, Object obj){".toCharArray();    
    private static final char[] HEADER_METHOD_SERIALIZE_TO_BYTE_ARRAY = "public final byte[] serialize(Object obj){".toCharArray();
    private static final char[] HEADER_METHOD_RESET = "public final void reset(Object obj){".toCharArray();
    private static final char[] HEADER_METHOD_COMPARE = "public final java.util.List compare(Object obj1,Object obj2){".toCharArray();
    private static final char[] HEADER_METHOD_CLONE = "public final Object clone(Object objSource){".toCharArray();

    
    private static final char[] HEADER_METHOD_NEW_INSTANCE_NO_PARAM="public final Object newInstance(){".toCharArray();
    private static final char[] CODE_IF = "if(\"".toCharArray();
    private static final char[] CODE_ELSE_IF = "else if(\"".toCharArray();
    private static final char[] CODE_EQ_PROPERTY_NAME = "\".equals(propertyName))".toCharArray();
    private static final char[] CODE_DECLARE_FINAL_KEYWORD="final ".toCharArray();
    private static final char[] CODE_DOUBLE_END_OF="}}".toCharArray();
    private static final char[] CODE_DECLARE_FINAL_MAP="final java.util.Map mapReturn=new java.util.HashMap(".toCharArray();
    private static final char[] CODE_LOCAL_VAR_O_DOT="o.".toCharArray();
    private static final char[] CODE_SET_LOCAL_VAR_O=" o=(".toCharArray();
    private static final char[] CODE_LOCAL_VAR_T_DOT="t.".toCharArray();
    private static final char[] CODE_SPACE_LOCAL_T=" t=(".toCharArray();
    private static final char[] CODE_LOCAL_VAR_RET="ret=".toCharArray();
    private static final char[] CODE_INVOKE_AUTOBOXING_O="autoboxing(o.".toCharArray();
    private static final char[] END_METHOD_RETURN_EMPTY_COLLECTION_MAP = "return java.util.Collections.EMPTY_MAP;}".toCharArray();
    private static final char[] END_METHOD_RETURN_MAP_RETURN = "return mapReturn;}".toCharArray();
    private static final char[] END_METHOD_RETURN_NULL = "return null}".toCharArray();
    
    private static final char[] DOT_BOOLEAN_VALUE = ".booleanValue()".toCharArray();
    private static final char[] DOT_BYTE_VALUE = ".byteValue()".toCharArray();
    private static final char[] DOT_SHORT_VALUE = ".shortValue()".toCharArray();
    private static final char[] DOT_CHAR_VALUE = ".charValue()".toCharArray();
    private static final char[] DOT_INT_VALUE = ".intValue()".toCharArray();
    private static final char[] DOT_LONG_VALUE = ".longValue()".toCharArray();
    private static final char[] DOT_FLOAT_VALUE = ".floatValue()".toCharArray();
    private static final char[] DOT_DOUBLE_VALUE = ".doubleValue()".toCharArray();

    protected static final char[] BOOLEAN_DEFAULT_NAME="(BOOLEAN_DEFAULT);".toCharArray();
    protected static final char[] CHAR_DEFAULT_NAME="(CHAR_DEFAULT);".toCharArray();
    protected static final char[] BYTE_DEFAULT_NAME="(BYTE_DEFAULT);".toCharArray();
    protected static final char[] SHORT_DEFAULT_NAME="(SHORT_DEFAULT);".toCharArray();
    protected static final char[] INT_DEFAULT_NAME="(INT_DEFAULT);".toCharArray();    
    protected static final char[] LONG_DEFAULT_NAME="(LONG_DEFAULT);".toCharArray();
    protected static final char[] FLOAT_DEFAULT_NAME="(FLOAT_DEFAULT);".toCharArray();
    protected static final char[] DOUBLE_DEFAULT_NAME="(DOUBLE_DEFAULT);".toCharArray();
    
    static {
        try {
            clParent = classPool.get(BeanDescriptor.class.getName());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private BeanUtility() {
        throw new RuntimeException("Can't create instance of "+BeanUtility.class.getName());
    }

    public static <T> BeanDescriptor<T> getDescriptor(Class<T> clazz) {
        BeanDescriptor beanDescriptor = mapBeanDescriptor.get(clazz);
        if (beanDescriptor == null) {
            if(clazz.isPrimitive()||clazz.isAnnotation()||clazz.isInterface()||clazz.isArray()){
                return null;
            }
            final List<Method> listSetter = new ArrayList<>(), listGetter = new ArrayList<>();
            final List<Constructor> listConstructor = new ArrayList<>();
            Constructor constructorNoParameter=collectAllConstructor(clazz, listConstructor);
            collectAllMethod(clazz, listSetter, listGetter);
            try {
                CtClass ctClass = classPool.makeClass(PREFIX_CLASS_NAME_GEN + clazz.getSimpleName() + SUFFIX_CLASS_NAME_GET, clParent);
                ctClass.setModifiers(CLASS_MODIFIER);
                ctClass.setGenericSignature(clazz.getName());
                StringBuilder sb = new StringBuilder(1024);
                createNewMethodNewInstance(sb, ctClass, clazz, constructorNoParameter);
                createNewMethodSerialize(sb, ctClass, clazz, listGetter);
                createNewMethodReset(sb, ctClass, clazz, listSetter, listGetter);
                createNewMethodSerializeToByte(sb, ctClass, clazz, listGetter);
                createNewMethodDescribe(sb, ctClass, clazz, listGetter);
                createNewMethodDescribeNotNullProperty(sb, ctClass, clazz, listGetter);
                createNewMethodCopy(sb, ctClass, clazz, listSetter, listGetter);
                createNewMethodCopyPropertyWhenNotNull(sb, ctClass, clazz, listSetter, listGetter);
                createNewMethodCopyWithIgnoreProperties(sb, ctClass, clazz, listSetter, listGetter);
                createNewMethodInvokeGetter(sb, ctClass, clazz, listGetter);
                createNewMethodInvokeSetter(sb, ctClass, clazz, listSetter);
                createNewMethodCopyFromMap(sb, ctClass, clazz, listSetter, listGetter);
                createNewMethodToString(sb, ctClass, clazz, listGetter);
                createNewMethodCompare(sb, ctClass, clazz, listGetter);
                createNewMethodClone(sb, ctClass, clazz, listSetter, listGetter,constructorNoParameter);
                ctClass.writeFile(("D:\\"));
                beanDescriptor = (BeanDescriptor<T>) ctClass.toClass().getDeclaredConstructor(Class.class).newInstance(clazz);
                mapBeanDescriptor.put(clazz, beanDescriptor);
                sb.setLength(0);
                sb=null;
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return beanDescriptor;
    }

    /**
     *
     * @param clz
     * @param listSetter
     * @param listGetter
     */
    private static void collectAllMethod(Class clz, List<Method> listSetter, List<Method> listGetter) {
        while (clz != Object.class) {
            Method[] methods = clz.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (method.getModifiers() == Modifier.PUBLIC && method.getParameterTypes().length == 0 && !listGetter.contains(method)) {
                    if (methodName.startsWith(PREFIX_GETTER) || methodName.startsWith(PREFIX_IS)) {
                        if (!methodName.equals("getClass")) {
                            listGetter.add(method);
                        }
                    }
                } else if (method.getModifiers() == Modifier.PUBLIC && method.getParameterTypes().length == 1 && !listSetter.contains(method)) {
                    if (methodName.startsWith(PREFIX_SETTER)) {
                        listSetter.add(method);
                    }
                }
            }
            clz = clz.getSuperclass();
        }
    }

    /**
     * 
     * @param clz
     * @param listConstructor
     * @return 
     */
    private static Constructor collectAllConstructor(Class clz, List<Constructor> listConstructor) {
        Constructor ret=null;
        Constructor[] constructors = clz.getDeclaredConstructors();
        if (constructors != null) {
            for (Constructor constructor : constructors) {
                if ((constructor.getModifiers() & Modifier.PUBLIC) != 0) {
                    if(constructor.getParameterTypes()==null || constructor.getParameterTypes().length==0){
                       ret=constructor; 
                    }
                    else{
                        listConstructor.add(constructor);                        
                    }
                }
            }
        }
        return ret;
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodInvokeGetter(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_INVOKE_GETTER);
        sb.append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
        sb.append("Object ret=null;");
        if (!listGetter.isEmpty()) {
            int indexNumber = 0;
            for (Method method : listGetter) {
                String pgetName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                pgetName = pgetName.substring(0, 1).toLowerCase() + pgetName.substring(1);

                sb.append(indexNumber == 0 ? CODE_IF : CODE_ELSE_IF).append(pgetName).append(CODE_EQ_PROPERTY_NAME);
                if (method.getReturnType().isPrimitive()) {
                    sb.append(CODE_LOCAL_VAR_RET).append(CODE_INVOKE_AUTOBOXING_O).append(method.getName()).append("());");
                } else {
                    sb.append(CODE_LOCAL_VAR_RET).append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("();");
                }
                indexNumber++;
            }
            sb.append("return ret;}");
        } else {
            sb.append(END_METHOD_RETURN_NULL);
        }
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listSetter
     * @throws Throwable
     */
    private static void createNewMethodInvokeSetter(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listSetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_INVOKE_SETTER);
        sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
        if (!listSetter.isEmpty()) {
            int indexNumber = 0;
            for (Method method : listSetter) {
                String propertyName = method.getName().replace(PREFIX_SETTER, "");
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                if (method.getParameterTypes()[0].isPrimitive()) {
                    String parameterType = wrapPrimitif(method.getParameterTypes()[0]).getName();
                    sb.append(indexNumber == 0 ? CODE_IF : CODE_ELSE_IF).append(propertyName).append(CODE_EQ_PROPERTY_NAME);
                    sb.append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("(((").append(parameterType).append(")value)").append(toValue(method.getParameterTypes()[0])).append(");");
                } else {
                    String parameterType = method.getParameterTypes()[0].getName();
                    sb.append(indexNumber == 0 ? CODE_IF : CODE_ELSE_IF).append(propertyName).append("\".equals(propertyName) && (value instanceof ").append(parameterType).append("))");
                    sb.append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("((").append(parameterType).append(")value);");
                }
                indexNumber++;

            }
        }
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listSetter
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodCopyWithIgnoreProperties(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listSetter, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_COPY_IGNORE_PROPERTIES);
        if (!listGetter.isEmpty()) {
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")objSource;");
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SPACE_LOCAL_T).append(parameterClass.getName()).append(")objTarget;");
            sb.append("if(ignoreProperties==null || ignoreProperties.length==0){");
            for (Method method : listGetter) {
                String setName = PREFIX_SETTER + method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                Method validMethodSetter = null;
                for (Method methodSetter : listSetter) {
                    if (setName.equals(methodSetter.getName())) {
                        validMethodSetter = methodSetter;
                        break;
                    }
                }
                if (validMethodSetter != null) {
                    sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("(o.").append(method.getName()).append("());");
                }
            }
            sb.append("}else{");
            for (Method method : listGetter) {
                Method validMethodSetter = null;                            
                String propertyName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                String setName = PREFIX_SETTER + propertyName;
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                for (Method methodSetter : listSetter) {
                    if (setName.equals(methodSetter.getName())) {
                        validMethodSetter = methodSetter;
                        break;
                    }
                }
                if (validMethodSetter != null) {
                    sb.append(CODE_DECLARE_FINAL_KEYWORD).append(method.getReturnType().getName()).append(" $$_").append(propertyName).append(" = ").append(CODE_LOCAL_VAR_T_DOT).append(method.getName()).append("();");
                    sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("(o.").append(method.getName()).append("());");                    
                }
            }
            
            sb.append("final int ln=ignoreProperties.length;for(int i=0;i<ln;i++){");            
            int numberValidSetter = 0;
            for (Method method : listGetter) {
                Method validMethodSetter = null;                            
                String propertyName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                String setName = PREFIX_SETTER + propertyName;
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                for (Method methodSetter : listSetter) {
                    if (setName.equals(methodSetter.getName())) {
                        validMethodSetter = methodSetter;
                        break;
                    }
                }
                if (validMethodSetter != null) {
                    sb.append(numberValidSetter == 0 ? CODE_IF : CODE_ELSE_IF).append(propertyName).append("\".equals(ignoreProperties[i]))");
                    sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("($$_").append(propertyName).append(");");
                    numberValidSetter++;
                }
            }
            sb.append(CODE_DOUBLE_END_OF);
        }
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listSetter
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodCopy(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listSetter, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_COPY);
        if (!listGetter.isEmpty()) {
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")objSource;");
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SPACE_LOCAL_T).append(parameterClass.getName()).append(")objTarget;");
            for (Method method : listGetter) {
                String setName = PREFIX_SETTER + method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                Method validMethodSetter = null;
                for (Method methodSetter : listSetter) {
                    if (setName.equals(methodSetter.getName())) {
                        validMethodSetter = methodSetter;
                        break;
                    }
                }
                if (validMethodSetter != null) {
                    sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("(o.").append(method.getName()).append("());");
                }
            }
        }
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    
    /**
     * 
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listSetter
     * @param listGetter
     * @throws Throwable 
     */
    private static void createNewMethodCopyPropertyWhenNotNull(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listSetter, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_COPY_PROPERTY_WHEN_NOT_NULL);
        if (!listGetter.isEmpty()) {
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")objSource;");
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SPACE_LOCAL_T).append(parameterClass.getName()).append(")objTarget;");
            for (Method method : listGetter) {
                String setName = PREFIX_SETTER + method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                Method validMethodSetter = null;
                for (Method methodSetter : listSetter) {
                    if (setName.equals(methodSetter.getName())) {
                        validMethodSetter = methodSetter;
                        break;
                    }
                }
                if (validMethodSetter != null) {
                    if(method.getReturnType().isPrimitive()){
                        sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("(o.").append(method.getName()).append("());");                        
                    }
                    else{
                        sb.append("if(o.").append(method.getName()).append("()!=null)");
                        sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("(o.").append(method.getName()).append("());");                                                
                    }
                }
            }
        }
        sb.append("}");
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    
    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listSetter
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodCopyFromMap(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listSetter, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_COPY_FROM_MAP);
        if (!listGetter.isEmpty()) {
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SPACE_LOCAL_T).append(parameterClass.getName()).append(")objTarget;");
            sb.append("final java.util.Iterator it=c.entrySet().iterator();");
            sb.append("while(it.hasNext()){");
            sb.append("final java.util.Map.Entry e=(java.util.Map.Entry)it.next();");
            sb.append("final String propertyName=(String)e.getKey();");
            sb.append("final Object propertyValue=e.getValue();");
            int i = 0;
            for (Method method : listGetter) {
                String propertyName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                String setName = PREFIX_SETTER + propertyName;
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                Method validMethodSetter = null;
                for (Method methodSetter : listSetter) {
                    if (setName.equals(methodSetter.getName())) {
                        validMethodSetter = methodSetter;
                        break;
                    }
                }
                if (validMethodSetter != null) {
                    if (i == 0) {
                        sb.append("if(propertyName.equals(\"").append(propertyName).append("\")){");
                    } else {
                        sb.append("else if(propertyName.equals(\"").append(propertyName).append("\")){");
                    }
                    if (!validMethodSetter.getParameterTypes()[0].isPrimitive()) {
                        sb.append("if(propertyValue instanceof ").append(validMethodSetter.getParameterTypes()[0].getName()).append("){");
                        sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("((").append(validMethodSetter.getParameterTypes()[0].getName()).append(")propertyValue);}}");
                    } else {
                        sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("((((").append(wrapPrimitif(validMethodSetter.getParameterTypes()[0]).getName()).append(")propertyValue))").append(toValue(validMethodSetter.getParameterTypes()[0])).append(");}");
                    }
                    i++;

                }
            }
        }
        sb.append(CODE_DOUBLE_END_OF);
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodDescribeNotNullProperty(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_DESCRIBE_NOT_NULL);
        sb.append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
        if (!listGetter.isEmpty()) {
            sb.append(CODE_DECLARE_FINAL_MAP).append(listGetter.size()).append(");");
            for (Method method : listGetter) {
                String pgetName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                pgetName = pgetName.substring(0, 1).toLowerCase() + pgetName.substring(1);
                if (method.getReturnType().isPrimitive()) {
                    sb.append("mapReturn.put(\"").append(pgetName).append("\",").append(CODE_INVOKE_AUTOBOXING_O).append(method.getName()).append("()));");
                } else {
                    sb.append("if(o.").append(method.getName()).append("()!=null)");
                    sb.append("mapReturn.put(\"").append(pgetName).append("\",o.").append(method.getName()).append("());");
                }
            }
            sb.append(END_METHOD_RETURN_MAP_RETURN);
        } else {
            sb.append(END_METHOD_RETURN_EMPTY_COLLECTION_MAP);
        }
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodDescribe(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_DESCRIBE);
        sb.append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
        if (!listGetter.isEmpty()) {
            sb.append(CODE_DECLARE_FINAL_MAP).append(listGetter.size()).append(");");
            for (Method method : listGetter) {
                String pgetName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                pgetName = pgetName.substring(0, 1).toLowerCase() + pgetName.substring(1);
                if (method.getReturnType().isPrimitive()) {
                    sb.append("mapReturn.put(\"").append(pgetName).append("\",").append(CODE_INVOKE_AUTOBOXING_O).append(method.getName()).append("()));");
                } else {
                    sb.append("mapReturn.put(\"").append(pgetName).append("\",o.").append(method.getName()).append("());");
                }
            }
            sb.append(END_METHOD_RETURN_MAP_RETURN);
        } else {
            sb.append(END_METHOD_RETURN_EMPTY_COLLECTION_MAP);
        }
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodToString(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEAEDER_METHOD_TO_STRING);
        sb.append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
        if (!listGetter.isEmpty()) {
            sb.append("final StringBuilder sb=new StringBuilder(").append(listGetter.size()).append(");").append("sb.append(\"").append(parameterClass.getName()).append("{\");");
            for (Method method : listGetter) {
                String pgetName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                pgetName = pgetName.substring(0, 1).toLowerCase() + pgetName.substring(1);
                sb.append("sb.append(\"").append(pgetName).append("=\").append(o.").append(method.getName()).append("()).append(',');");
            }
            sb.append("sb.setLength(sb.length()-1);");
            sb.append("return sb.append('}').toString();};");
        } else {
            sb.append("return \"").append(parameterClass.getName()).append("{}\"}");
        }
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodSerialize(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_SERIALIZE);
        sb.append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
        if (!listGetter.isEmpty()) {
            for (Method method : listGetter) {
                Class type = method.getReturnType();
                if (type.isPrimitive()) {
                    if (type == boolean.class) {
                        sb.append("objectOutput.writeBoolean(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == char.class) {
                        sb.append("objectOutput.writeChar(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == byte.class) {
                        sb.append("objectOutput.writeByte(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == short.class) {
                        sb.append("objectOutput.writeShort(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == int.class) {
                        sb.append("objectOutput.writeInt(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }else if (type == long.class) {
                        sb.append("objectOutput.writeLong(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }else if (type == float.class) {
                        sb.append("objectOutput.writeFloat(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }else if (type == double.class) {
                        sb.append("objectOutput.writeDouble(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }
                } else {
                    sb.append("objectOutput.writeObject(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                }
            }
        }
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     * 
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listGetter
     * @throws Throwable 
     */
    private static void createNewMethodCompare(StringBuilder sb, CtClass clz, Class parameterClass,  List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_COMPARE);
        if (!listGetter.isEmpty()) {
            sb.append("java.util.List list=new java.util.ArrayList(").append(listGetter.size()).append(");");                                
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj1;");
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SPACE_LOCAL_T).append(parameterClass.getName()).append(")obj2;");
            for (Method method : listGetter) {
                String pgetName = method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                pgetName = pgetName.substring(0, 1).toLowerCase() + pgetName.substring(1);
                if(method.getReturnType().isPrimitive()){
                    sb.append("if(o.").append(method.getName()).append("()!=t.").append(method.getName()).append("()){");
                    sb.append("list.add(new ").append(Result.class.getName()).append("(\"").append(pgetName).append("\",autoboxing(o.").append(method.getName()).append("()),autoboxing(t.").append(method.getName()).append("())));");
                    sb.append("}");
                }
                else{
                    sb.append("if(o.").append(method.getName()).append("()!=null){");                    
                    sb.append("if(!o.").append(method.getName()).append("().equals(t.").append(method.getName()).append("())){");
                    sb.append("list.add(new ").append(Result.class.getName()).append("(\"").append(pgetName).append("\",o.").append(method.getName()).append("(),t.").append(method.getName()).append("()));");
                    sb.append("}");
                    sb.append("}");
                    sb.append("else{");
                    sb.append("if(t.").append(method.getName()).append("()!=null){");                    
                    sb.append("list.add(new ").append(Result.class.getName()).append("(\"").append(pgetName).append("\",null,t.").append(method.getName()).append("()));");
                    sb.append("}");                    
                    sb.append("}");
                    
                }
            }
            sb.append("return list;");
        }
        else{
            sb.append("return java.util.Collections.emptyList();");            
        }
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    
    /**
     *
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listGetter
     * @throws Throwable
     */
    private static void createNewMethodSerializeToByte(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_SERIALIZE_TO_BYTE_ARRAY);
        sb.append("try{");
        sb.append("final java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();");
        sb.append("final java.io.ObjectOutputStream objectOutput=new java.io.ObjectOutputStream(baos);");
        sb.append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
        if (!listGetter.isEmpty()) {
            for (Method method : listGetter) {
                Class type = method.getReturnType();
                if (type.isPrimitive()) {
                    if (type == boolean.class) {
                        sb.append("objectOutput.writeBoolean(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == char.class) {
                        sb.append("objectOutput.writeChar(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == byte.class) {
                        sb.append("objectOutput.writeByte(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == short.class) {
                        sb.append("objectOutput.writeShort(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    } else if (type == int.class) {
                        sb.append("objectOutput.writeInt(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }else if (type == long.class) {
                        sb.append("objectOutput.writeLong(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }else if (type == float.class) {
                        sb.append("objectOutput.writeFloat(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }else if (type == double.class) {
                        sb.append("objectOutput.writeDouble(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                    }
                } else {
                    sb.append("objectOutput.writeObject(").append(CODE_LOCAL_VAR_O_DOT).append(method.getName()).append("());");
                }
            }
        }
        sb.append("return baos.toByteArray();");
        sb.append('}');
        sb.append("catch(java.io.IOException e){");
        sb.append("throw new RuntimeException(e);");
        sb.append('}');
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }
    
    /**
     * 
     * @param sb
     * @param clz
     * @param parameterClass
     * @param constructorNoParameter
     * @throws Throwable 
     */
    private static void createNewMethodNewInstance(StringBuilder sb, CtClass clz, Class parameterClass, Constructor constructorNoParameter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_NEW_INSTANCE_NO_PARAM);
        if(constructorNoParameter==null){
            sb.append("return null;");
        }
        else{
            sb.append("return new ").append(parameterClass.getName()).append("();");
        }
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }

    /**
     * 
     * @param sb
     * @param clz
     * @param parameterClass
     * @param listSetter
     * @param listGetter
     * @throws Throwable 
     */
    private static void createNewMethodReset(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listSetter, List<Method> listGetter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_RESET);
        if (!listGetter.isEmpty()) {
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")obj;");
            for (Method method : listGetter) {
                String setName = PREFIX_SETTER + method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                Method validMethodSetter = null;
                for (Method methodSetter : listSetter) {
                    if (setName.equals(methodSetter.getName())) {
                        validMethodSetter = methodSetter;
                        break;
                    }
                }
                if (validMethodSetter != null) {
                    if(method.getReturnType().isPrimitive()){
                        Class returnType=method.getReturnType();
                        if(returnType==boolean.class){
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(BOOLEAN_DEFAULT_NAME);                                                    
                        }
                        else if(returnType==char.class){
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(CHAR_DEFAULT_NAME);                                                    
                        }
                        else if(returnType==byte.class){
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(BYTE_DEFAULT_NAME);                                                    
                        }
                        else if(returnType==short.class){
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(SHORT_DEFAULT_NAME);                                                    
                        }
                        else if(returnType==int.class){
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(INT_DEFAULT_NAME);                                                    
                        }
                        else if(returnType==long.class){
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(LONG_DEFAULT_NAME);                                                    
                        }
                        else if(returnType==float.class){
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(FLOAT_DEFAULT_NAME);                                                    
                        }
                        else{
                            sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append(DOUBLE_DEFAULT_NAME);                                                                                
                        }
                    }
                    else{
                        sb.append(CODE_LOCAL_VAR_O_DOT).append(validMethodSetter.getName()).append("(null);");                        
                    }
                }
            }
        }
        sb.append('}');
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }


    private static void createNewMethodClone(StringBuilder sb, CtClass clz, Class parameterClass, List<Method> listSetter, List<Method> listGetter,Constructor constructorNoParameter) throws Throwable {
        sb.setLength(0);
        sb.append(HEADER_METHOD_CLONE);
        if(constructorNoParameter==null){
            sb.append("return null;}");
            return;
        }
        else{
            sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(" t=new ").append(parameterClass.getName()).append("();");
            if (!listGetter.isEmpty()) {
                sb.append(CODE_DECLARE_FINAL_KEYWORD).append(parameterClass.getName()).append(CODE_SET_LOCAL_VAR_O).append(parameterClass.getName()).append(")objSource;");
                for (Method method : listGetter) {
                    String setName = PREFIX_SETTER + method.getName().replace(PREFIX_IS, "").replace(PREFIX_GETTER, "");
                    Method validMethodSetter = null;
                    for (Method methodSetter : listSetter) {
                        if (setName.equals(methodSetter.getName())) {
                            validMethodSetter = methodSetter;
                            break;
                        }
                    }
                    if (validMethodSetter != null) {
                        sb.append(CODE_LOCAL_VAR_T_DOT).append(validMethodSetter.getName()).append("(o.").append(method.getName()).append("());");
                    }
                }
            }
            sb.append("return t;}");
            
        }
        CtMethod ctNewMethod = CtNewMethod.make(sb.toString(), clz);
        clz.addMethod(ctNewMethod);
    }
    
    /**
     *
     * @param clz
     * @return
     */
    private static Class wrapPrimitif(Class clz) {
        if (boolean.class == clz) {
            return Boolean.class;
        } else if (short.class == clz) {
            return Short.class;
        } else if (byte.class == clz) {
            return Byte.class;
        } else if (char.class == clz) {
            return Character.class;
        } else if (int.class == clz) {
            return Integer.class;
        } else if (long.class == clz) {
            return Long.class;
        } else if (float.class == clz) {
            return Float.class;
        } else if (double.class == clz) {
            return Double.class;
        }
        throw new RuntimeException("Class " + clz.getName() + " is not primitive!");
    }

    /**
     *
     * @param clz
     * @return
     */
    private static char[] toValue(Class clz) {
        if (boolean.class == clz) {
            return DOT_BOOLEAN_VALUE;
        } else if (short.class == clz) {
            return DOT_SHORT_VALUE;
        } else if (byte.class == clz) {
            return DOT_BYTE_VALUE;
        } else if (char.class == clz) {
            return DOT_CHAR_VALUE;
        } else if (int.class == clz) {
            return DOT_INT_VALUE;
        } else if (long.class == clz) {
            return DOT_LONG_VALUE;
        } else if (float.class == clz) {
            return DOT_FLOAT_VALUE;
        } else if (double.class == clz) {
            return DOT_DOUBLE_VALUE;
        }
        throw new RuntimeException(clz.getName() + " not define toValue!");
    }
    
    public static void main(String[] args) {
        BeanUtility.getDescriptor(String.class);
    }
}
