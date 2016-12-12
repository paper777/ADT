package com.wuzhy.tree;

import com.wuzhy.tree.RBTree;

import org.junit.Test;
import org.junit.Assert;

public class RBTreeTest
{

	@Test
	public void testInsert() {
	    RBTree<Integer> tree = new RBTree<Integer>();
		tree.insert(20);
		tree.insert(15);
		tree.insert(25);
		tree.insert(10);
		Assert.assertEquals(tree.root.color, RBTree.Node.BLACK);
		Assert.assertEquals((int) tree.root.value, 20);
		Assert.assertEquals(tree.root.left.color, RBTree.Node.BLACK);
		Assert.assertEquals(tree.root.right.color, RBTree.Node.BLACK);
		Assert.assertEquals((int) tree.root.left.value, 15);
		Assert.assertEquals((int) tree.root.right.value, 25);
		Assert.assertEquals((int) tree.root.left.left.value, 10);
		Assert.assertEquals(tree.root.left.left.color, RBTree.Node.RED);

		tree.insert(17);
		tree.insert(8);
		tree.insert(9);
		Assert.assertEquals((int) tree.getMinimum(), 8);
		Assert.assertEquals(tree.header.left.color, RBTree.Node.RED);

		Assert.assertEquals(tree.header.left.parent.color, RBTree.Node.BLACK);
		Assert.assertEquals((int) tree.header.left.parent.value, 9);

		Assert.assertEquals(tree.header.left.parent.right.color, RBTree.Node.RED);
		Assert.assertEquals((int) tree.header.left.parent.right.value, 10);

		tree.delete(9);
		Assert.assertEquals((int) tree.getMinimum(), 8);


	}

}
