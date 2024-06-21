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

    private PagedList<Integer> commonPagedList;

    @BeforeEach
    void setUp() {
        List<Integer> commonList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
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

    @Test
    void whenHasNextPageCalledAndCurrentPageIsOne_thenReturnTrue() {
        assertTrue(commonPagedList.hasNextPage());
    }

    @Test
    void whenHasNextPageCalledAndCurrentPageIsTwo_thenReturnFalse() {
        commonPagedList.setCurrentPage(2);
        assertFalse(commonPagedList.hasNextPage());
    }

    @Test
    void whenHasPreviousPageCalledAndCurrentPageIsOne_thenReturnFalse() {
        assertFalse(commonPagedList.hasPreviousPage());
    }

    @Test
    void whenHasPreviousPageCalledAndCurrentPageIsGreaterThanOne_thenReturnTrue() {
        commonPagedList.setCurrentPage(2);
        assertTrue(commonPagedList.hasPreviousPage());
    }
}
