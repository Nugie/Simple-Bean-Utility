/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baculsoft.beanutils.test;

import com.baculsoft.beanutils.BeanDescriptor;
import com.baculsoft.beanutils.BeanUtility;
import com.baculsoft.beanutils.Result;
import com.baculsoft.beanutils.test.pojo.PojoExample;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Natalino Nugeraha
 */
public class TestBeanUtili {

    private final int countBeanNotNull = 8;
    private final int countBeanProp = 18;

    private BeanDescriptor<PojoExample> pojoDescriptor = BeanUtility.getDescriptor(PojoExample.class);

    public TestBeanUtili() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testDescribeNotNull() {
        PojoExample pojoExample = new PojoExample();
        assertEquals(8, pojoDescriptor.describeNotNullProperty(pojoExample).size());
    }

    @Test
    public void testDescribe() {
        PojoExample pojoExample = new PojoExample();
        assertEquals(18, pojoDescriptor.describe(pojoExample).size());
    }

    @Test
    public void testCopy() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 11));

        PojoExample pojoTarget = new PojoExample();
        pojoDescriptor.copy(pojoSource, pojoTarget);

        assertEquals(pojoDescriptor.describe(pojoSource), pojoDescriptor.describe(pojoTarget));
    }

    @Test
    public void testCopyPropertyWhenNotNull() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 11));

        PojoExample pojoTarget = new PojoExample();
        pojoTarget.setIntProp1(500);
        pojoDescriptor.copyPropertyWhenNotNull(pojoSource, pojoTarget);

        assertNotEquals(pojoDescriptor.describe(pojoSource), pojoDescriptor.describe(pojoTarget));
        assertEquals(pojoDescriptor.invokeGetter("intProp1", pojoSource), null);
        assertNotEquals(pojoDescriptor.invokeGetter("intProp1", pojoTarget), null);
    }

    @Test
    public void testCopy2() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 12));

        PojoExample pojoTarget = new PojoExample();
        pojoDescriptor.copy(pojoSource, pojoTarget);

        assertEquals(pojoDescriptor.describeNotNullProperty(pojoSource), pojoDescriptor.describeNotNullProperty(pojoTarget));
    }

    @Test
    public void testCopyIgnoreProperties() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 12));

        PojoExample pojoTarget = new PojoExample();
        pojoDescriptor.copy(pojoSource, pojoTarget, new String[]{"stringProp"});
        pojoSource.setStringProp(null);

        assertEquals(pojoDescriptor.describeNotNullProperty(pojoSource), pojoDescriptor.describeNotNullProperty(pojoTarget));
    }

    @Test
    public void testCopyFromMap() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 12));

        Map describe = pojoDescriptor.describe(pojoSource);

        PojoExample copyPojo = new PojoExample();
        pojoDescriptor.copyFromMap(describe, copyPojo);
        assertEquals(pojoDescriptor.describe(pojoSource), pojoDescriptor.describe(copyPojo));
        assertEquals(describe, pojoDescriptor.describe(copyPojo));
        assertEquals(pojoDescriptor.describe(pojoSource), describe);

    }

    @Test
    public void testCopyFromMap2() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 12));

        Map describe = pojoDescriptor.describe(pojoSource);

        PojoExample copyPojo = new PojoExample();
        pojoDescriptor.copyFromMap(describe, copyPojo);
        assertEquals(pojoDescriptor.describeNotNullProperty(pojoSource), pojoDescriptor.describeNotNullProperty(copyPojo));
    }

    @Test
    public void testToString() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 15));
        PojoExample pojoTarget = new PojoExample();
        pojoDescriptor.copy(pojoSource, pojoTarget);
        assertEquals(pojoDescriptor.toString(pojoSource), pojoDescriptor.toString(pojoTarget));
    }

    @Test
    public void testNewInstance() {
        assertNotNull(pojoDescriptor.newInstance());
    }

    @Test
    public void testWriteObjectOutputStream() throws Throwable {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 15));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        pojoDescriptor.serialize(oos, pojoSource);
        assertNotSame(baos.toByteArray().length, 0);
    }

    @Test
    public void testBeanToByteArray() throws Throwable {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 15));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        pojoDescriptor.serialize(oos, pojoSource);
        assertArrayEquals(baos.toByteArray(), pojoDescriptor.serialize(pojoSource));
    }

    @Test
    public void testReset() throws Throwable {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 15));

        pojoSource.setIntProp1(new Integer(15));
        pojoSource.setLongProp1(new Long(15));
        pojoSource.setFloatProp1(new Float(15));

        pojoDescriptor.reset(pojoSource);

        PojoExample pojoDefault = new PojoExample();
        assertEquals(pojoDescriptor.describe(pojoDefault), pojoDescriptor.describe(pojoSource));
        assertEquals(pojoDescriptor.toString(pojoDefault), pojoDescriptor.toString(pojoSource));

        PojoExample pojoDefault2 = pojoDescriptor.newInstance();
        assertEquals(pojoDescriptor.describe(pojoDefault2), pojoDescriptor.describe(pojoSource));
        assertEquals(pojoDescriptor.toString(pojoDefault2), pojoDescriptor.toString(pojoSource));

    }

    
    @Test
    public void testCompare() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 12));

        PojoExample pojoTarget = new PojoExample();
        pojoDescriptor.copy(pojoSource, pojoTarget);

        List<Result> result=pojoDescriptor.compare(pojoSource, pojoTarget);
        assertEquals(result.size(),0);

        
        pojoSource.setDateProp(null);
        
        result=pojoDescriptor.compare(pojoSource, pojoTarget);
        assertEquals(result.size(),1);
        System.out.println(result);
        
    }

    
    @Test
    public void testClone() {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 12));
        pojoSource.setCharProp('A');
        pojoSource.setCharProp1(new Character('A'));
        pojoSource.setIntProp(560);
        pojoSource.setIntProp1(new Integer(333));
        pojoSource.setLongProp(5999l);
        pojoSource.setLongProp1(new Long(5999l));

        
        PojoExample pojoTarget = pojoDescriptor.clone(pojoSource);

        List<Result> result=pojoDescriptor.compare(pojoSource, pojoTarget);
        assertEquals(result.size(),0);

        
        pojoSource.setDateProp(null);
        
        result=pojoDescriptor.compare(pojoSource, pojoTarget);
        assertEquals(result.size(),1);

        
        pojoSource.setBooleanProp(false);
        
        result=pojoDescriptor.compare(pojoSource, pojoTarget);
        assertEquals(result.size(),2);
        
    }
    
}
