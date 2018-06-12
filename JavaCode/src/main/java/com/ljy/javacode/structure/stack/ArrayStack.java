package com.ljy.javacode.structure.stack;

/**
 * Created by ljy on 2018/5/31.
 *
 * 使用数组实现栈
 *
 * 栈也可以用链表来实现
 *
 * 后进先出 LIFO （last in first out）
 */

public class ArrayStack<T> extends Stack<T> {

    protected int maxSize;
    protected int top;
    private T[] stackArray;

    public ArrayStack(int maxSize){
        stackArray= (T[]) new Object[maxSize];
        this.maxSize=maxSize;
        top=-1;
    }

    @Override
    public boolean isEmpty(){
        return top==-1;
    }

    public boolean isFull(){
        return top==maxSize-1;
    }

    @Override
    public void push(T value){
        stackArray[++top]=value;
    }

    @Override
    public T pop(){
        return stackArray[top--];
    }

    @Override
    public T peek(){
        return stackArray[top];
    }


    /**
     * 打印数组
     */
    @Override
    public void display() {
        System.out.print("ArrayStacker: ");
        for (int i = 0; i <=top; i++) {
            System.out.print("[" + i + "]-->" + stackArray[i] + "  ");
        }
        System.out.println();

    }

}