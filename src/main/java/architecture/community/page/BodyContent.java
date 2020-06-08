package architecture.community.page;

public interface BodyContent {

    public long getBodyId();

    public void setBodyId(long bodyId);

    public long getPageId();

    public void setPageId(long pageId);

    public BodyType getBodyType();

    public void setBodyType(BodyType bodyType);

    public String getBodyText();

    public void setBodyText(String bodyText);

}