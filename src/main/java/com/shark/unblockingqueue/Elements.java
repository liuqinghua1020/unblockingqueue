package com.shark.unblockingqueue;

/**
 * Created by qinghualiu on 2018/10/22.
 */
public class Elements<T> {

    Element<T>[] elements;

    int size;
    int indexMask;

    public Elements(int size, int indexMask){
        //TODO check size and indexMask
        this.size = size;
        this.indexMask = indexMask;
        this.elements = new Element[size];
    }

    void addElement(int i, Element element){
        this.elements[i] = element;
    }


    Element<T> elementAt(long sequence){
        int index = (int)(sequence & indexMask);
        //TODO check index;
        return elements[index];
    }

    void setData(long sequence, T data){
        int index = (int)(sequence & indexMask);
        elements[index].setValue(data);
    }

}
