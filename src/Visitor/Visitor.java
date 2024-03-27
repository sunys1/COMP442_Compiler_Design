package Visitor;

import java.io.IOException;
import AstGeneration.Node;

public interface Visitor {
    public void visit(Node node) throws IOException;
}