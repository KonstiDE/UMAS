package wue.eorc.umas.utils;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public class AgiSoftList implements ObservableList<String> {

    private final List<String> list;

    public AgiSoftList(List<String> list) {
        this.list = list;
    }

    public static AgiSoftList of(String... e) {
        return new AgiSoftList(Arrays.asList(e));
    }

    @Override
    public void addListener(ListChangeListener<? super String> listChangeListener) {
        throw new UnsupportedOperationException("Not implemented for this kind of list.");
    }

    @Override
    public void removeListener(ListChangeListener<? super String> listChangeListener) {
        throw new UnsupportedOperationException("Not implemented for this kind of list.");
    }

    @Override
    public boolean addAll(String... strings) {
        return list.addAll(Arrays.asList(strings));
    }

    @Override
    public boolean setAll(String... strings) {
        throw new UnsupportedOperationException("Not implemented for this kind of list.");
    }

    @Override
    public boolean setAll(Collection<? extends String> collection) {
        throw new UnsupportedOperationException("Not implemented for this kind of list.");
    }

    @Override
    public boolean removeAll(String... strings) {
        return list.removeAll(Arrays.asList(strings));
    }

    @Override
    public boolean retainAll(String... strings) {
        return list.retainAll(Arrays.asList(strings));
    }

    @Override
    public void remove(int i, int i1) {
        throw new UnsupportedOperationException("Not implemented for this kind of list.");
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(String s) {
        return list.add(s);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(list).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends String> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AgiSoftList strings)) return false;

        return Objects.equals(list, strings.list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public String get(int index) {
        return list.get(index);
    }

    @Override
    public String set(int index, String element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, String element) {
        list.add(index, element);
    }

    @Override
    public String remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<String> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<String> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        throw new UnsupportedOperationException("Not implemented for this kind of list.");
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        throw new UnsupportedOperationException("Not implemented for this kind of list.");
    }

}
