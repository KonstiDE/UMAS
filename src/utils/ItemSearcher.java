package utils;

import controller.panes.views.ViewController;
import enums.ErrorType;
import exception.UMASException;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public class ItemSearcher {

    public static <T extends Node> T getItemById(String query, Parent root, Class<T> type) throws UMASException {
        ArrayList<Node> list = getAllNodes(root);

        Optional<Node> item = list.stream()
                .filter(e -> e.getId() != null && e.getId().equals(query))
                .findFirst();

        if (item.isPresent()) {
            Node found = item.get();
            if (type.isInstance(found)) {
                return type.cast(found);
            } else {
                throw new UMASException(ErrorType.INTERNAL, "Item found but is not of type: \"" + type.getSimpleName() + "\"");
            }
        } else {
            throw new UMASException(ErrorType.INTERNAL, "Could not find item with the id of \"" + query + "\"");
        }

    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

}
