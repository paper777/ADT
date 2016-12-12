package com.wuzhy.tree;

import java.lang.Comparable;


/**
 * Created by also on 2016/12/9.
 */
class RBTree<V extends Comparable<V>> {

    protected Node<V> header;

    protected Node<V> root;

    protected Node<V> current;

    private int size;

    public RBTree() {
        init();
    }

    public int getSize() {
        return this.size;
    }

    public V getMinimum() {
        return this.header.left != null ? (V) this.header.left.value : null;
    }

    public V getMaximum() {
        return this.header.right != null ? (V) this.header.right.value : null;
    }

    public boolean contains(V value) {
        Node<V> x = root;
        while (x != null) {
            int comp = x.value.compareTo(value);
            if (comp == 0) {
                return true;
            } else if (comp > 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        return false;
    }

    public void flush() {
        current = header.left;
    }

    public V next() {
        Node<V> n = null;
        if (current == null) {
            current = header.left;
            n = header.left;
        } else {
            n = getNext(current);
            current = n;
        }
        return n == null ? null : n.value;
    }

    public boolean insert(V value) {
        Node<V> y = header;
        Node<V> x = root;

        int comp = 0;
        while (x != null) {
            y = x;
            comp = x.value.compareTo(value);
            if (comp ==0) return false;
            x = comp > 0  ? x.left : x.right;
        }

        Node<V> node = new Node<V>(value);

        node.parent = y;
        if (y == header) {
            root = node;
            root.parent = header;
            header.parent
                    = header.left
                    = header.right
                    = node;
        } else if (comp > 0) { // left node
            y.left = node;
            if (header.left == y) {
                header.left = node;
            }
        } else {
            y.right = node;
            if (header.right == y) {
                header.right = node;
            }
        }

        size ++;

        rebalance(node);

        return true;
    }

    private void rebalance(Node<V> newer) {
        Node<V> p = newer;
        while (p != root && p.parent.color == Node.RED) {
            Node<V> parent = p.parent;
            if (parent == parent.parent.left) { // LL style
                Node<V> uncle = parent.parent.right;
                if (uncle != null && uncle.color == Node.RED) { // uncle color is red
                    uncle.color = parent.color = Node.BLACK;
                    uncle.parent.color = Node.RED;
                    p = parent.parent;
                } else { // uncle is colored black or null
                    if (p == parent.left) {
                        parent.color = Node.BLACK;
                        parent.parent.color = Node.RED;
                        lrotate(parent.parent);
                    } else {
                        parent.parent.color = Node.RED;
                        p.color = Node.BLACK;
                        Node<V> g = parent.parent;
                        lrotate(parent);
                        rrotate(g);
                    }
                }

            } else { // RR style
                Node<V> uncle = parent.parent.right;
                if (uncle != null && uncle.color == Node.RED) { // uncle color is red
                    uncle.color = parent.color = Node.BLACK;
                    uncle.parent.color = Node.RED;
                    p = parent.parent;
                } else {
                    if (p == parent.right) {
                        parent.color = Node.BLACK;
                        parent.parent.color = Node.RED;
                        rrotate(parent.parent);
                    } else {
                        parent.parent.color = Node.RED;
                        p.color = Node.BLACK;
                        Node<V> g = parent.parent;
                        rrotate(parent);
                        lrotate(g);
                    }
                }
            }
        } // while loop

        root.color = Node.BLACK;
    }

    private Node<V> rrotate(Node<V> node) {
        Node<V> l = node.left;
        boolean left = node.parent.left == node ? true : false;

        node.left = l.right;
        if (node.left != null) {
            node.left.parent = node;
        }

        l.parent = node.parent;
        if (l.parent == header) {
            header.parent = l;
            root = l;
        } else if (left) {
            l.parent.left = l;
        } else {
            l.parent.right = l;
        }

        l.right = node;
        node.parent = l;

        return l;
    }

    private Node<V> lrotate(Node<V> node) {
        Node<V> r = node.right;
        boolean left = node.parent.left == node ? true : false;

        node.right = r.left;
        if (node.right != null) {
            node.right.parent = node;
        }

        r.parent = node.parent;
        if (r.parent == header) {
            header.parent = r;
            root = r;
        } else if (left) {
            r.parent.left = r;
        } else {
            r.parent.right = r;
        }

        r.left = node;
        node.parent = r;
        return r;
    }

    public boolean delete(V value) {
        Node<V> x = root;

        int comp = 0;
        while (x != null) {
            comp = x.value.compareTo(value);
            if (comp == 0) {
                break;
            } else if (comp > 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        if (x == null) {
            return false;
        }

        if (x.left == null && x.right == null) { // left node
            Node<V> parent = x.parent;
            if (parent.left == x) {
                parent.left = null;
                if (header.left == x) {
                    header.left = parent;
                }
            } else {
                parent.right = null;
                if (header.right == x) {
                    header.right = parent;
                }
            }
            x.parent = null;
        } else if (x.left != null && x.right == null
                || x.left == null && x.right != null) { // has one child
            Node<V> parent = x.parent;
            Node<V> child = x.left != null ? x.left : x.right;

            if (parent.left == x) {
                parent.left = child;
                child.parent = parent;
            } else {
                parent.right = child;
                child.parent = parent;
            }

            if (x.color == Node.BLACK) {
                child.color = Node.BLACK;
            }

            x.parent = x.left = null;
        } else {
            Node<V> next = getNext(x);
            delete(next.value);
            x.value = next.value;
        }
        return true;

    }

    private Node<V> getNext(Node<V> current) {
        Node<V> p = current;
        if (p == header.right) {
            return null;
        }
        if (p.right != null) {
            p = p.right;
            while (p.left != null) {
                p = p.left;
            }
            return p;
        } else {
            while (p != header && p.value.compareTo(current.value) <= 0) {
                p = p.parent;
            }
            if (p == header) {
                return null;
            }
            return p;
        }
    }


    protected void init() {
        this.header = new Node<V>();
        this.header.left
                = this.header.right
                = this.header;
//        this.header.parent
//                = this.root;

    }


    public class Node<V extends Comparable<V>>
    {

        public static final int RED = 0;

        public static final int BLACK = 1;

        public V value;

        public Node<V> parent;

        public Node<V> left;

        public Node<V> right;

        public int color;

        public Node() {
            this.color = Node.RED;
        }

        public Node(V value) {
            this.value = value;
            this.color = Node.RED;
        }

        public int compareTo(Node<V> other) throws Exception {
            if (value != null) {
                return this.value.compareTo((V) other.value);
            }
            throw new Exception("Null node value Exception");
        }

    }
}
