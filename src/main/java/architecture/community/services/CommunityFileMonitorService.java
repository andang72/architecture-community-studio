package architecture.community.services;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.ee.service.Repository;

public class CommunityFileMonitorService {
	
	private Logger log = LoggerFactory.getLogger(CommunityFileMonitorService.class);
	
	public static final int DEFAULT_POOL_INTERVAL_MILLIS = 5*1000;
	
	private FileAlterationMonitor monitor; 
	
	@Inject
	@Qualifier("repository")
	private Repository repository;
	
	public CommunityFileMonitorService() {
		
	}
	
	public void initialize() throws Exception {
		File dir = repository.getFile("services-config");
		log.debug("initailizing {} with {}" , this.getClass().getName(), dir.getAbsolutePath()); 
		if( dir.exists() ) {
			start( dir );
		} 
	}
	
	public void addListener(File dir, FileAlterationListener listener ){
		if( monitor != null) { 
			for( FileAlterationObserver observer : monitor.getObservers() ) {
				
				try {
					if( observer.getDirectory().getCanonicalPath().equals( dir.getCanonicalPath() ) )
					{
						log.debug("adding file alteration listener {} for {}", listener.getClass().getName(), dir.getPath() );
						observer.addListener(listener);
						break;
					}
				} catch (IOException e) {
				}
			}
		}
	}
	
	public void start(File file ) throws Exception { 
		log.debug("starting config observer ... '{}' ", file.getAbsolutePath());
		if( monitor == null)
		{			
			monitor = new FileAlterationMonitor(DEFAULT_POOL_INTERVAL_MILLIS);	
			FileAlterationObserver observer = new FileAlterationObserver(file);
			monitor.addObserver(observer);
		} 
		monitor.start();
		log.debug("started dataservice config observer ...  '{0}' ", file.getAbsolutePath());
	}
	
	public void destroy() throws Exception { 
		if( monitor != null)
		{
			log.debug("stopping dataservice config monitor ...");
			monitor.stop();
			log.debug("stopped dataservice config monitor ...");
		}
	}
}
