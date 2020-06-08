package architecture.community.page;


public class DefaultBodyContent implements BodyContent {

	private long bodyId;

	private long pageId;

	private BodyType bodyType;

	private String bodyText;

	private String firstImageSrc = null;

	private int imageCount = 0;

	public DefaultBodyContent() {
		this.bodyId = -1L;
		this.pageId = -1L;
		this.bodyType = BodyType.FREEMARKER;
	}

	public DefaultBodyContent(long pageId, String bodyText) {
		this.bodyId = -1L;
		this.pageId = pageId;
		this.bodyType = BodyType.FREEMARKER;
		setBodyText(bodyText);
	}

	/**
	 * @param bodyId
	 * @param pageId
	 * @param bodyType
	 * @param bodyText
	 */
	public DefaultBodyContent(long bodyId, long pageId, BodyType bodyType, String bodyText) {
		this.bodyId = bodyId;
		this.pageId = pageId;
		this.bodyType = bodyType;
		setBodyText(bodyText);
	}

	/**
	 * @return bodyText
	 */
	public String getBodyText() {
		return bodyText;
	}

	/**
	 * @param bodyText
	 *            설정할 bodyText
	 */
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
		try {
			//Document doc = Jsoup.parse(this.bodyText);
			//Elements links = doc.select("img");
			//this.imageCount = links.size();
			//if (imageCount > 0)
			//	firstImageSrc = links.first().attr("src");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return bodyId
	 */
	public long getBodyId() {
		return bodyId;
	}

	/**
	 * @param bodyId
	 *            설정할 bodyId
	 */
	public void setBodyId(long bodyId) {
		this.bodyId = bodyId;
	}

	/**
	 * @return pageId
	 */
	public long getPageId() {
		return pageId;
	}

	/**
	 * @param pageId
	 *            설정할 pageId
	 */
	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	/**
	 * @return bodyType
	 */
	public BodyType getBodyType() {
		return bodyType;
	}

	/**
	 * @param bodyType
	 *            설정할 bodyType
	 */
	public void setBodyType(BodyType bodyType) {
		this.bodyType = bodyType;
	}

	/**
	 * @return firstImageSrc
	 */
	public String getFirstImageSrc() {
		return firstImageSrc;
	}

	/**
	 * @return imageCount
	 */
	public int getImageCount() {
		return imageCount;
	}

}
