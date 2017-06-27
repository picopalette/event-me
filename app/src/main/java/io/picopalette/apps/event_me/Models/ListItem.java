package io.picopalette.apps.event_me.Models;

/**
 * Created by Aswin Sundar on 27-06-2017.
 */

public class ListItem {
    private String id;
    private String itemName;
    private boolean isDone;

    public ListItem() {
    }

    public ListItem(String id, String itemName, boolean isDone) {
        this.id = id;
        this.itemName = itemName;
        this.isDone = isDone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
}
