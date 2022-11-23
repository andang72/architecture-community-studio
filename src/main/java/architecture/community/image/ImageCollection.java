package architecture.community.image;

public class ImageCollection {
    
    private String title ;

    private String description ;

    private boolean shared;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public String getDescription() {
        return description;
    }

    public boolean isShared() {
        return shared;
    }


    
}
