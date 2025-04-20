package com.honwhy.examples.mybatis.service;

import com.honwhy.examples.BaseTest;
import com.honwhy.examples.configure.IncludeTest;
import org.junit.experimental.categories.Category;
import org.junit.Test;

@Category(IncludeTest.class)
public class StudentServiceTest extends BaseTest {

    @Test
    public void test1() {
        System.out.println("StudentServiceTest works");
    }
}
