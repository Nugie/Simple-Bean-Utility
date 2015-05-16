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
package com.baculsoft.beanutils.test;

import com.baculsoft.beanutils.BeanDescriptor;
import com.baculsoft.beanutils.BeanUtility;
import com.baculsoft.beanutils.test.pojo.PojoExample;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author Natalino Nugeraha
 */
public class TestBeanUtil2 {

    private BeanDescriptor<PojoExample> pojoDescriptor = BeanUtility.getDescriptor(PojoExample.class);

    public TestBeanUtil2() {
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

    @Test
    public void testCopy() throws Throwable{
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 11));
        pojoSource.setIntProp1(new Integer(15125));
        pojoSource.setLongProp1(new Long(20053215235l));
        pojoSource.setFloatProp1(new Float(20053215235f));

        PojoExample pojoTarget = new PojoExample();
        pojoDescriptor.copy(pojoSource, pojoTarget);
        pojoDescriptor.reset(pojoTarget);

        PojoExample pojoTarget2 = new PojoExample();
        PojoExample pojoTarget3 = new PojoExample();
        
        BeanUtils.copyProperties(pojoSource, pojoTarget2);
        org.apache.commons.beanutils.BeanUtils.copyProperties(pojoSource,pojoTarget3);

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            pojoDescriptor.copy(pojoSource, pojoTarget);

        }
        t1 = System.currentTimeMillis() - t1;

        long t2 = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            BeanUtils.copyProperties(pojoSource, pojoTarget2);
        }
        t2 = System.currentTimeMillis() - t2;

        
        long t3 = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            org.apache.commons.beanutils.BeanUtils.copyProperties(pojoSource,pojoTarget3);
        }
        t3 = System.currentTimeMillis() - t3;
        
        System.out.println("Time to copy properties ");
        System.out.println("this class: t1 ---> " + t1);
        System.out.println("spring-beans: t2 --->" + t2);
        System.out.println("apache commons: t3 --->" + t3);
        
        System.out.println(pojoDescriptor.describe(pojoTarget).equals(pojoDescriptor.describe(pojoTarget2)));

        assertEquals(pojoDescriptor.describe(pojoTarget), pojoDescriptor.describe(pojoTarget2));
    }

    @Test
    public void testDescribe() throws Throwable {
        PojoExample pojoSource = new PojoExample();
        pojoSource.setStringProp("String Property");
        pojoSource.setBooleanProp(true);
        pojoSource.setBooleanProp1(Boolean.TRUE);
        pojoSource.setDateProp(new Date());
        pojoSource.setByteProp1(new Byte((byte) 11));
        pojoSource.setIntProp1(new Integer(15125));
        pojoSource.setLongProp1(new Long(20053215235l));
        pojoSource.setFloatProp1(new Float(20053215235f));

        pojoDescriptor.describe(pojoSource);

        org.apache.commons.beanutils.BeanUtils.describe(pojoSource);

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            pojoDescriptor.describe(pojoSource);

        }
        t1 = System.currentTimeMillis() - t1;

        long t2 = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            org.apache.commons.beanutils.BeanUtils.describe(pojoSource);
        }
        t2 = System.currentTimeMillis() - t2;

        System.out.println("Time to describe ");
        System.out.println("this class : t1 --->" + t1);
        System.out.println("apache commons : t2 --->" + t2);
        System.out.println(pojoDescriptor.describe(pojoSource).equals(pojoDescriptor.describe(pojoSource)));

        assertEquals(pojoDescriptor.describe(pojoSource), pojoDescriptor.describe(pojoSource));
    }

}
