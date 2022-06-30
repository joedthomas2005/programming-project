package game.engine.rendering;

public class Node<T> {
    public Node(T value){
        this.value = value;
        this.next = null;
    }
    public T value;
    public Node<T> next;
}
