package com.wuzhy.tree;

import java.lang.Comparable;
import java.lang.Exception;

class AVLTree<V extends Comparable<V>>
{

    protected Node<V> header;  

    protected Node<V> root; // first node 

    protected Node<V> current; // simple iterator

    protected int size;


    public AVLTree() {
        init();
    }

    public int size() {
        return size;
    }

    public V getMaximum() {
        return header.right == null ? null : header.right.value;
    }

    public V getMinimum() {
        return header.left == null ? null : header.left.value;
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

    // flush iterator
    public void flush() {
        current = header.left;
    }

    public boolean contains(V value) {
        Node<V> y = header;
        Node<V> x = root;

        while (x != null) {
            y = x;
            int comp = x.value.compareTo(value);
            if (comp > 0) {
                x = x.left;
            } else if (comp < 0) {
                x = x.right;
            } else {
                break;
            }
        }
        return x != null ? true : false;
    }

    public boolean insert(V value) {
        Node<V> y = header;
        Node<V> x = root;

        int comp = 0;
        while (x != null) {
            y = x;
            comp = x.value.compareTo(value);
            if (comp == 0) {
                return false;
            }
            x = comp > 0 ? x.left : x.right;
        }

        // insert
        Node<V> node = new Node<V>(value);
        node.parent = y;
        if (y == header || comp > 0) {
            y.left = node;
            if (y == header) {
                root = node;
                header.parent = root;
                header.right = root;
                root.parent = header;
            } else if (header.left == y) {
                header.left = node;
            } 
        } else {
            y.right = node;
            if (y == header.right) {
                header.right = node;
            } 
        }

        size ++;

        // rebalance
        boolean rb = false;
        while (y != header && ! rb) {
            if (y.value.compareTo(node.value) > 0) {
                switch (y.bf) {
                    case Node.LH:
                        leftBalance(y);
                        rb = true;
                        break;

                    case Node.RH:
                        y.bf = Node.EH;
                        break;

                    case Node.EH:
                        y.bf = Node.LH;
                        break;

                    default: break;
                } // switch
            } else {
                switch (y.bf) {
                    case Node.LH:
                        y.bf = Node.EH;
                        break;

                    case Node.RH:
                        rightBalance(y);
                        rb = true;
                        break;

                    case Node.EH:
                        y.bf = Node.RH;
                        break;

                    default: break;
                }
            }

            y = y.parent;
        }

        return true;
    }

    public boolean delete(V value) {
        Node<V> node = root;

        int comp = 0;
        while (node != null) {
            comp = node.value.compareTo(value);
            if (comp == 0) {
                break;
            }
            node = comp > 0 ? node.left : node.right;
        }

        if (node == null) {
            return false;
        }

        if (node.left == null && node.right == null) {
            Node<V> parent = node.parent;
            if (header.left == node) {
                header.left = parent;
            } else if (header.right == node) {
                header.right = parent;
            }

            if (parent.left == node) {
                parent.left = null;
                switch (parent.bf) {
                    case Node.EH:
                    case Node.LH:
                        parent.bf = Node.RH;
                        break;

                    case Node.RH:
                        rightBalance(parent);
                        break;

                    default: break;
                }
            } else {
                parent.right = null;
                switch (parent.bf) {
                    case Node.EH:
                    case Node.RH:
                        parent.bf = Node.RH;
                        break;

                    case Node.LH:
                        leftBalance(parent);
                        break;

                    default: break;
                }
            }
        } // if leaf node
        else if (node.left != null && node.right == null
                || node.left == null && node.right != null) {
            Node<V> parent = node.parent;
            Node<V> child = node.left != null ? node.left : node.right;
            if (parent.left == node) {
                parent.left = child;
                child.parent = parent;

                switch (parent.bf) {
                    case Node.EH:
                        parent.bf = Node.RH;
                        break;

                    case Node.LH:
                        parent.bf = Node.EH;
                        break;

                    case Node.RH:
                        rightBalance(parent);
                        break;
                }
            } else {
                parent.right = child;
                child.parent = parent;

                switch (parent.bf) {
                    case Node.EH:
                        parent.bf = Node.LH;
                        break;

                    case Node.RH:
                        parent.bf = Node.EH;
                        break;

                    case Node.LH:
                        leftBalance(parent);
                        break;
                }
            }
        } else {
            Node<V> next = getNext(node);
            delete(next.value);
            node.value = next.value;
        }
        size --;
        return true;
    }

    protected void leftBalance(Node<V> node) {
        Node<V> l = node.left;
        switch (l.bf) {
            case Node.LH: // LL style
                node.bf = l.bf = Node.EH;
                rrotate(node);
                break;

            case Node.RH: // LR style
                Node<V> lr = l.right;
                switch (lr.bf) {
                    case Node.EH:
                        node.bf = l.bf = Node.EH;
                        break;

                    case Node.LH:
                        l.bf = lr.bf = Node.EH;
                        node.bf = Node.RH;
                        break;

                    case Node.RH:
                        l.bf = lr.bf = Node.EH;
                        node.bf = Node.LH;
                        break;

                    default:break;
                } // inner switch
                lrotate(l);
                rrotate(node);
                break;

            default: break;
        }
    }

    protected void rightBalance(Node<V> node) {
        Node<V> r = node.right;
        switch (r.bf) {
            case Node.RH: // RR style
                node.bf = r.bf = Node.EH;
                lrotate(node);
                break;
            
            case Node.LH: // RL style
                Node<V> rl = r.left;
                switch (rl.bf) {
                    case Node.EH:
                        node.bf = r.bf = rl.bf = Node.EH;
                        break;

                    case Node.RH:
                        rl.bf = node.bf = Node.EH;
                        r.bf = Node.LH;
                        break;

                    case Node.LH:
                        rl.bf = node.bf = Node.EH;
                        r.bf = Node.RH;
                        break;

                    default:break;
                } // inner switch
                rrotate(r);
                lrotate(node);
                break;
                
            default: break;
        }

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

    public void printTree() {
        System.out.println("PreOrder:");
        printPreorder(root);
        System.out.println();


        System.out.println("InOrder:");
        printInorder(root);
        System.out.println();

        System.out.println("PostOrder:");
        printPostorder(root);
        System.out.println();
    }

    protected void init() {
        this.header = new Node<V>();
        this.header.left 
            = this.header.right 
            = this.header
            ;
        this.current = this.header.left;
    }

    private void printInorder(Node<V> node) {
        if (node == null) return;
        printInorder(node.left);
        if (node.value != null) {
            System.out.print(node.value + " ");
        } else {
            System.out.print("<null> ");
        }
        printInorder(node.right);
    }

    private void printPreorder(Node<V> node) {
        if (node == null) return;
        if (node.value != null) {
            System.out.print(node.value + " ");
        } else {
            System.out.print("<null> ");
        }
        printPreorder(node.left);
        printPreorder(node.right);
    }

    private void printPostorder(Node<V> node) {
        if (node == null) return;

        printPostorder(node.left);
        printPostorder(node.right);

        if (node.value != null) {
            System.out.print(node.value + " ");
        } else {
            System.out.print("<null> ");
        }
    }


    public class Node<V extends Comparable<V>>
    {
        public static final int LH = 1;
        public static final int EH = 0;
        public static final int RH = -1;

        public V value;

        /**
         * bf int balance factor
         */
        public int bf;

        public Node<V> parent;

        public Node<V> left;

        public Node<V> right;

        public Node() { }

        public Node(V value) {
            this.value = value;
        }

        public int compareTo(Node<V> other) throws Exception {
            if (this.value != null) {
                return this.value.compareTo(other.value);
            }
            throw new Exception("Null node value Exception.");
        }

        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

    }
}
