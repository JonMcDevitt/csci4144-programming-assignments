package Assignment3;

/**
 * Created by Owner on 2017-02-25.
 */
public class ItemNode {
    private String header;
    private String value;

    public ItemNode(String header, String value) {
        this.header = header;
        this.value = value;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(ItemNode n) {
        return header.compareTo(n.getHeader()) == 0 && value.compareTo(n.getValue()) == 0;
    }

    @Override
    public String toString() {
        return header + " = " + value;
    }
}
