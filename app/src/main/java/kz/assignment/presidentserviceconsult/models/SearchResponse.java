package kz.assignment.presidentserviceconsult.models;

import java.util.List;

public class SearchResponse<T> {
    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
