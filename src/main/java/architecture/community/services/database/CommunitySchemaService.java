package architecture.community.services.database;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.community.services.CommunityFileMonitorService;
import architecture.ee.service.Repository;

/**
 * Manages database schemas for Openfire and Openfire plugins. The manager uses the
 * ofVersion database table to figure out which database schema is currently installed
 * and then attempts to automatically apply database schema changes as necessary.<p>
 *
 * Running database schemas automatically requires appropriate database permissions.
 * Without those permissions, the automatic installation/upgrade process will fail
 * and users will be prompted to apply database changes manually.
 *
 * @see DbConnectionManager#getSchemaManager()
 *
 * @author Matt Tucker
 */
public class CommunitySchemaService {

	private Logger log = LoggerFactory.getLogger(CommunityFileMonitorService.class);

	@Inject
	@Qualifier("repository")
	private Repository repository;	
	
	
	
	
}