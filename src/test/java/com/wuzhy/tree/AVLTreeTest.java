package com.wuzhy.tree;

import com.wuzhy.tree.AVLTree;

import org.junit.Test;
import org.junit.Assert;

public class AVLTreeTest
{

	@Test
	public void testInsert() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		tree.insert(20);
		tree.insert(15);
		tree.insert(25);
        Assert.assertEquals((int) tree.root.left.value, 15);
        Assert.assertEquals((int) tree.root.right.value, 25);
        Assert.assertEquals(tree.root.bf, 0);
		
		tree.insert(22);
        Assert.assertEquals(tree.root.bf, -1);
		tree.insert(21);

        Assert.assertEquals(tree.root.bf, -1);
        Assert.assertEquals(tree.root.right.bf, 0);
        Assert.assertEquals(tree.root.right.left.bf, 0);

		Assert.assertEquals(tree.size, 5);
		Assert.assertEquals((int) tree.root.value, 20);
		Assert.assertEquals((int) tree.root.left.value, 15);
	}

    @Test
    public void testInsertRotate() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(50);
        tree.insert(25);
        tree.insert(70);
        tree.insert(20);
        tree.insert(15);
        Assert.assertEquals((int) tree.root.left.value, 20);

        tree.insert(17);

        Assert.assertEquals((int) tree.root.value, 20);

        tree.insert(10);
        tree.printTree();
		Assert.assertEquals(tree.size, 7);
		Assert.assertEquals((int)tree.root.value, 20);

		Assert.assertEquals((int)tree.getMinimum(), 10);
		Assert.assertEquals((int)tree.getMaximum(), 70);

		Assert.assertEquals((int)tree.root.left.value, 15);
		Assert.assertEquals((int)tree.root.right.value, 50);

		Assert.assertEquals((int)tree.root.left.left.value, 10);
		Assert.assertEquals((int)tree.root.left.right.value, 17);

		Assert.assertEquals((int)tree.root.right.left.value, 25);
		Assert.assertEquals((int)tree.root.right.right.value, 70);



    }


	@Test
	public void testDelete() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		tree.insert(20);
		tree.insert(15);
		tree.insert(25);
		tree.insert(23);

        Assert.assertEquals(tree.root.bf, -1);
        Assert.assertEquals(tree.root.left.bf, 0);
        Assert.assertEquals(tree.root.right.bf, 1);
        Assert.assertEquals(tree.root.right.left.bf, 0);
		Assert.assertEquals(tree.size, 4);

		tree.delete(15); // root is now unbalanced rotation performed

		Assert.assertEquals(tree.size, 3);
		Assert.assertEquals(tree.root.value, (Integer)23); // new root
		Assert.assertEquals(tree.root.left.value, (Integer)20);
		Assert.assertEquals(tree.root.right.value, (Integer)25);

		//testTreeBSTProperties(tree.root);
	}


}
