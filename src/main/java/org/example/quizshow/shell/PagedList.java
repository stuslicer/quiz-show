package org.example.quizshow.shell;

import java.util.*;
import java.util.function.Consumer;


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

    /**
     * Returns a new list that contains all the elements from the original list.
     *
     * @return a new list containing all the elements from the original list
     */
    public List<T> getFullList() {
        return new ArrayList<>(this.list);
    }

    /**
     * Performs the given action for each element in the paged list.
     * The action is applied to each element in the sublist of elements based on the current page and page size.
     *
     * @param action the action to be performed on each element
     * @throws NullPointerException if the specified action is null
     */
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : getList()) {
            action.accept(t);
        }
    }

    public int totalPages() {
        return (int) Math.ceil((double) list.size() / pageSize);
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if there is a previous page, false otherwise
     */
    public boolean hasPreviousPage() {
        return this.currentPage > 1;
    }

    /**
     * Checks if there is a next page.
     *
     * @return true if there is a next page, false otherwise
     */
    public boolean hasNextPage() {
        return this.currentPage < totalPages();
    }


    /**
     * Returns the size of the paged list.
     *
     * @return the size of the paged list
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns whether the paged list is empty or not.
     *
     * @return true if the paged list is empty, false otherwise
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
