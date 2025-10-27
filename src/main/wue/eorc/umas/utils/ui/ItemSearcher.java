package wue.eorc.umas.utils.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.controlsfx.control.CheckComboBox;
import wue.eorc.umas.enums.ErrorType;
import wue.eorc.umas.exception.UMASException;

import java.util.ArrayList;
import java.util.Optional;

public class ItemSearcher {

    public static <T extends Node> T getItemById(String query, Parent root, Class<T> type) throws UMASException {
        if(root instanceof DialogPane){
            if(((DialogPane) root).getContent() != null){
                root = (Parent) ((DialogPane) root).getContent();
            }
        }

        ArrayList<Node> list = getAllNodes(root);
        if(root instanceof ScrollPane)
            list.add(((ScrollPane) root).getContent());

        list.add(root);

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

    @SuppressWarnings("unchecked")
    public static <T extends Control, G, R extends T> R getGenericControlById(String id, Parent root, Class<T> type, Class<G> genericType) throws UMASException {
        Node node = getItemByIdWithGeneric(id, root, type, genericType);

        return (R) node;
    }

    private static <T extends Control, G> T getItemByIdWithGeneric(String query, Parent root, Class<T> type, Class<G> genericType) throws UMASException {
        if(root instanceof DialogPane) {
            root = (Parent) ((DialogPane) root).getContent();
        }

        ArrayList<Node> list = getAllNodes(root);

        Optional<Node> item = list.stream()
                .filter(e -> e.getId() != null && e.getId().equals(query))
                .findFirst();

        if (item.isPresent()) {
            Node found = item.get();
            if (type.isInstance(found)) {
                T casted = type.cast(found);

                if (genericType != null) {
                    boolean valid = switch (casted) {
                        case ComboBox<?> combo ->
                                combo.getItems().isEmpty() || genericType.isInstance(combo.getItems().get(0));
                        case TableView<?> table ->
                                table.getItems().isEmpty() || genericType.isInstance(table.getItems().get(0));
                        case CheckComboBox<?> checkcombo ->
                                checkcombo.getItems().isEmpty() || genericType.isInstance(checkcombo.getItems().get(0));
                        case TreeView<?> treeView ->
                                treeView.getRoot() == null || genericType.isInstance(treeView.getRoot().getChildren().get(0));
                        case ListView<?> listView ->
                                listView.getItems().isEmpty() || genericType.isInstance(listView.getItems().get(0));
                        default ->
                                throw new UMASException(ErrorType.INTERNAL, "Generic check not supported for type: " + type.getSimpleName());
                    };

                    if (!valid) {
                        throw new UMASException(ErrorType.INTERNAL,
                                "Generic type of component doesn't match expected: " + genericType.getSimpleName());
                    }
                }

                return casted;
            } else {
                throw new UMASException(ErrorType.INTERNAL,
                        "Item found but is not of type: \"" + type.getSimpleName() + "\"");
            }
        } else {
            throw new UMASException(ErrorType.INTERNAL,
                    "Could not find item with the id of \"" + query + "\"");
        }
    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<>();
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
