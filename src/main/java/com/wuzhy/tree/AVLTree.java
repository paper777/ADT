package com.wuzhy.tree;

import java.lang.Comparable;

class AVLTree<V extends Comparable<V>>
{

    protected Node<V> header;  

    protected Node<V> root; // first node 

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

    public boolean insertUnique(V value) {
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
            } else if (header.right == y) {
                header.right = node;
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
            if (comp > 0) {
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
        else if (node.left != null || node.right != null) {
            Node<V> parent = node.parent;
            if (parent.left == node) {
                parent.left = node.left;
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
                parent.right = node.left;
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
            Node<V> next = getNext(node); // TODO
            delete(next.value);
            node.value = next.value;
        }
        return true;
    }

    protected void leftBalance(Node<V> node) {
        Node<V> l = node.left;
        switch (l.bf) {
            case Node.LH: // LL style
                node.bf = l.bf = Node.EH;
                lrotate(node);
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
                rrotate(node);
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
        node.left = l.right;
        l.right = node;

        l.parent = node.parent;
        node.parent = l;

        return l;
    }

    private Node<V> lrotate(Node<V> node) {
        Node<V> r = node.right;
        node.right = r.left;
        r.left = node;

        r.parent = node.parent;
        node.parent = r;

        return r;
    }


    private Node<V> search(V value) {
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
        return x;
    }

    private Node<V> getNext(Node<V> current) {
        Node<V> p = current;
        if (p.right != null) {
            p = p.right;
            while (p != null) {
                p = p.left;
            }
            return p;
        } else {
            Node<V> parent = p.parent;
            while (parent != header && parent.left != p) {
                parent = parent.parent;
            }
            return parent;
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

    private void init() {
        this.header = new Node<V>();
        this.header.left 
            = this.header.right 
            = this.header
            ;
    }

    private void printInorder(Node<V> node) {
        if (node == null) return;
        printInorder(node.left);
        if (node.value != null) {
            System.out.println(node.value + " ");
        } else {
            System.out.println("<null> ");
        }
        printInorder(node.right);
    }

    private void printPreorder(Node<V> node) {
        if (node == null) return;
        if (node.value != null) {
            System.out.println(node.value + " ");
        } else {
            System.out.println("<null> ");
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

        public int compareTo(Node<V> other) {
            return this.value.compareTo(other.value);
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
