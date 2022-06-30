package game.engine.rendering;

import java.util.*;
import java.util.function.Consumer;

public class FastLinkedList<T> {
    private int size;
    private final Node<T> head;
    private Node<T> tail;

    public FastLinkedList(){
        this.head = new Node<>(null);
        this.tail = head;
    }

    public void add(T o){
        tail.next = new Node<>(o);
        tail = tail.next;
        size++;
    }

    public void forEach(Consumer<T> consumer){
        Node<T> cur = head.next;
        while(cur != null){
            consumer.accept(cur.value);
            cur = cur.next;
        }
    }

    public void clear(){
        head.next = null;
        tail = head;
        size = 0;
    }

    public int size(){
        return size;
    }
}
