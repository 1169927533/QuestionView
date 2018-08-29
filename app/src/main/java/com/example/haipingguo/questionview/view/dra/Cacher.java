package com.example.haipingguo.questionview.view.dra;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Cacher<T,P> implements ICacher<T, P>{

    private static final int DEFAULT_MAX_POOL_SIZE = 10;

    private final AtomicInteger mMaxPoolSize = new AtomicInteger();
    private Node<T> node;
    private int mCurrentPoolSize;

    public Cacher(int maxPoolSize) {
        this.node = new Node<T>();
        this.mMaxPoolSize.set(maxPoolSize);
    }
    public Cacher(){
        this(DEFAULT_MAX_POOL_SIZE);
    }
    public void setMaxPoolSize(int maxPoolSize){
        this.mMaxPoolSize.set(maxPoolSize);
    }
    public int getMaxPoolSize() {
        return mMaxPoolSize.get();
    }

    public int getCurrentPoolSize(){
        return mCurrentPoolSize;
    }

    public void prepare(){
        prepare(null);
    }
    @Override
    public void prepare(P p) {
        synchronized (this) {
            final int max = this.getMaxPoolSize() ;
            Node<T> n     = this.node ;
            int current   = this.mCurrentPoolSize ;

            while(current < max){
                if(n.t == null){
                    n.t = create(p);
                }else{
                    Node<T> n1 = new Node<T>();
                    n1.next = n;
                    n1.t = create(p);
                    n = n1; //new node is the front
                }
                current++ ;
            }
            this.node = n;
            this.mCurrentPoolSize = current;
        }
    }

    public T obtain(){
        return obtain(null);
    }

    @Override
    public T obtain(P p) {
        synchronized (this) {
            if(node.t!=null){
                Node<T> tmp = this.node;
                T t = tmp.t;
                node = tmp.next;
                //may null
                if(node == null){
                    node = new Node<T>();
                }
                tmp.next = null;
                mCurrentPoolSize --;
                return t;
            }
        }
        return create(p);
    }

    public void recycle(T t){
        synchronized (this) {
            final int max = getMaxPoolSize();
            if (mCurrentPoolSize < max) {
                Node<T> nodeNew = new Node<T>();
                nodeNew.next = node ;
                nodeNew.t = t;
                this.node = nodeNew;
                mCurrentPoolSize ++ ;
                onRecycleSuccess(t);
            }
        }
    }
    public void clear(){
        synchronized (this) {
            Node<T> node = this.node;
            while( node != null ){
                node.t = null;
                node = node.next;
            }
            this.node = new Node<T>();
            mCurrentPoolSize = 0;
        }
    }


    protected void onRecycleSuccess(T t){ }

    @SuppressWarnings("hiding")
    public class Node<T>{
        T t;
        Node<T> next;
    }

}
