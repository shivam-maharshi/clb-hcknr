package com.javacoders.websocketizer.parser;

import com.github.javaparser.ast.Node;

/**
 * Author: dedocibula
 * Created on: 17.10.2016.
 */
public class NodeIterator {
    public interface NodeHandler {
        boolean handle(Node node);
    }

    private NodeHandler nodeHandler;

    public NodeIterator(NodeHandler nodeHandler) {
        this.nodeHandler = nodeHandler;
    }

    public void explore(Node node) {
        if (nodeHandler.handle(node)) {
            node.getChildrenNodes().forEach(this::explore);
        }
    }
}
