package architecture.community.security.spring.acls;

public class PermissionsBundle {
	
	protected boolean read = false;
	protected boolean write = false;
	protected boolean create = false;
	protected boolean delete = false;
	protected boolean admin = false;
	protected boolean createThread = false;
	protected boolean createThreadMessage = false;
	protected boolean createAttachment = false;
	protected boolean createImage = false;
	protected boolean createComment = false;
	protected boolean readComment = false;

	public boolean isRead() {

		return read;
	}

	public boolean isWrite() {
		return write;
	}

	public boolean isCreate() {
		return create;
	}

	public boolean isDelete() {
		return delete;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isCreateThread() {
		return createThread;
	}

	public boolean isCreateThreadMessage() {
		return createThreadMessage;
	}

	public boolean isCreateAttachment() {
		return createAttachment;
	}

	public boolean isCreateImage() {
		return createImage;
	}

	public boolean isCreateComment() {
		return createComment;
	}

	public boolean isReadComment() {
		return readComment;
	}
}
