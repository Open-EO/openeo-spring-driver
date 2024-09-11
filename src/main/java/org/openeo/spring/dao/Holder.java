package org.openeo.spring.dao;

/**
 * Generic object holder: handy for capture conversion purposes.
 * @param <T>
 */
public class Holder<T> {
    private T value;
    public Holder() {}
    public Holder(T val) { value = val; }
    public void set(T val) { value = val; }
    public T get() { return value; }
    
    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }
}
