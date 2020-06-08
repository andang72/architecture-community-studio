package architecture.community.security.spring.acls;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.security.acls.domain.BasePermission;

public class CommunityPermissions extends BasePermission {

	
	public static final CommunityPermissions READ = new CommunityPermissions("READ", 1 << 0, 'R'); // 1
	public static final CommunityPermissions WRITE = new CommunityPermissions("WRITE", 1 << 1, 'W'); // 2
	public static final CommunityPermissions CREATE = new CommunityPermissions("CREATE", 1 << 2 , 'C'); // 4
	public static final CommunityPermissions DELETE = new CommunityPermissions("DELETE", 1 << 3 , 'D'); // 8
	public static final CommunityPermissions ADMINISTRATION = new CommunityPermissions("ADMIN", 1 << 4 , 'A'); // 16
	
	public static final CommunityPermissions CREATE_THREAD  = new CommunityPermissions("CREATE_THREAD", 1 << 5 , 'T'); 	// 32
	public static final CommunityPermissions CREATE_THREAD_MESSAGE  = new CommunityPermissions("CREATE_THREAD_MESSAGE", 1 << 6, 'M' ); 	// 64
	public static final CommunityPermissions READ_COMMENT = new CommunityPermissions("READ_COMMENT", 1 << 7 , 'c'); // 128
	public static final CommunityPermissions CREATE_COMMENT = new CommunityPermissions("CREATE_COMMENT", 1 << 8 , 'd'); // 256
	public static final CommunityPermissions CREATE_ATTACHMENT = new CommunityPermissions("CREATE_ATTACHMENT", 1 << 9 , 'F');	//512 
	public static final CommunityPermissions CREATE_IMAGE = new CommunityPermissions("CREATE_IMAGE", 1 << 10 , 'I'); //1024
	
	private static final CommunityPermissions [] permissions = {
		READ,
		WRITE,
		CREATE,
		DELETE,
		ADMINISTRATION,
		CREATE_THREAD, 
		CREATE_THREAD_MESSAGE, 
		READ_COMMENT, 
		CREATE_COMMENT, 
		CREATE_ATTACHMENT, 
		CREATE_IMAGE
	};
	 
	private String name;
	
	public CommunityPermissions(String name, int mask) {
		
		super(mask); 
		this.name = name;
	}

	public CommunityPermissions(String name, int mask, char code) {
		super(mask, code); 
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static CommunityPermissions[] values() {		
		return permissions;
	}
	
	public static CommunityPermissions getPermissionByMask(int mask) {
		CommunityPermissions permToUse = null;
		if( permToUse == null ) {
			for( CommunityPermissions p : CommunityPermissions.permissions ) {
				if ( p.mask == mask ) {
					permToUse = p;
					break;
				}
			}
		}
		return permToUse;
	}
	
	
	public static CommunityPermissions getPermissionByName(String name) {
		CommunityPermissions permToUse = null;
		if (StringUtils.equals(name, "ADMINISTRATION")){
			permToUse = CommunityPermissions.ADMINISTRATION;
		}
		if( permToUse == null ) {
			for( CommunityPermissions p : CommunityPermissions.permissions ) {
				if (StringUtils.equals(name, p.getName())) {
					permToUse = p;
					break;
				}
			}
		}
		return permToUse;
	}
}
