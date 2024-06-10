package org.example.quizshow.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PagedListTest {

    private List<Integer> commonList;
    private PagedList<Integer> commonPagedList;

    @BeforeEach
    void setUp() {
        commonList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        commonPagedList = new PagedList<>(commonList, 5);
    }

    @Test
    void whenSetCurrentPageInRange_thenSetSuccessfully() {
        commonPagedList.setCurrentPage(2);
        assertEquals(2, commonPagedList.currentPage());
    }

    @Test
    void whenSetCurrentPageLessThanOne_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> commonPagedList.setCurrentPage(0));
    }

    @Test
    void whenSetCurrentPageGreaterThanMax_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> commonPagedList.setCurrentPage(3));
    }
}