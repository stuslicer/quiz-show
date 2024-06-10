package org.example.quizshow.shell;

import java.util.*;


/**
 * Represents a paged list of elements.
 * The class provides methods to retrieve a sublist of elements based on the current page and page size.
 * Important to understand that page numbering begins at 1 not zero.
 *
 * @param <T> the type of elements in the paged list
 */
public class PagedList<T> {

    private final List<T> list;
    private final int pageSize;
    private int currentPage = 1; // pages are numbered from 1

    public PagedList(List<T> list, int pageSize) {
        this.list = list;
        this.pageSize = pageSize;
    }

    public int pageSize() {
        return pageSize;
    }

    public int currentPage() {
        return currentPage;
    }


    /**
     * Sets the current page number.
     *
     * @param currentPage the new page number to be set
     * @throws IllegalArgumentException if the page number is out of range
     */
    public void setCurrentPage(int currentPage) {
        int maxPage = list.size() / pageSize + (list.size() % pageSize == 0 ? -1 : 0);
        if (currentPage < 1 || currentPage > maxPage + 1) {
            throw new IllegalArgumentException(STR."Page number is out of range. It should be between 1 and \{maxPage + 1}");
        }
        this.currentPage = currentPage;
    }


    /**
     * Returns a sublist of the elements in the PagedArrayList based on the current page and page size.
     *
     * @return a sublist containing the elements in the current page
     */
    public List<T> getList() {
        int start = (currentPage - 1) * pageSize;
        return this.list.subList(start, Math.min(start + pageSize, list.size()));
    }

}
