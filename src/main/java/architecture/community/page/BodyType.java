package architecture.community.page;

public enum BodyType {

	RAW(1), HTML(2), FREEMARKER(3), VELOCITY(4);

	private int id;

	private BodyType(int id) {
		this.id = id;
	}

	/**
	 * @return id
	 */
	public int getId() {
		return id;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("BodyType:");
		switch (id) {
		case 1: // '\001'
			builder.append("RAW");
			break;

		case 2: // '\002'
			builder.append("HTML");
			break;
		case 3: // '\002'
			builder.append("FREEMARKER");
			break;
		case 4: // '\002'
			builder.append("VELOCITY");
			break;
		default:
			builder.append("id=").append(id);
			break;
		}
		return builder.toString();
	}

	public static BodyType getBodyTypeById(int typeId) {
		for (BodyType type : BodyType.values()) {
			if (type.getId() == typeId)
				return type;
		}
		return BodyType.RAW;
	}

}