/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.baculsoft.beanutils.test;

import com.baculsoft.beanutils.BeanDescriptor;
import com.baculsoft.beanutils.BeanUtility;
import com.baculsoft.beanutils.test.pojo.PojoExample;
import java.util.Date;
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
}