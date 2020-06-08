package architecture.community.services;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.management.ObjectName;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import architecture.ee.component.platform.DiskUsage;
import architecture.ee.component.platform.ManagementService;
import architecture.ee.component.platform.MemoryUsage;
import architecture.ee.component.platform.SystemInfo;
import architecture.ee.service.ConfigService;

public class CommunityManagementService implements InitializingBean, ManagementService {

	@Autowired
	private ConfigService configService;
	
	public CommunityManagementService() {

	}

	public void afterPropertiesSet() throws Exception {
		ManagementFactory.getRuntimeMXBean().getStartTime();
	
	}

	public List<DiskUsage> getDiskUsages() {

		File[] list = File.listRoots();
		List<DiskUsage> usages = new ArrayList<DiskUsage>(list.length);
		for (File file : list) {
			usages.add(DiskUsage.Builder.build(file));
		}
		return usages;
	}
 
	
	public MemoryUsage getMemoryUsage() {
		
		MemoryPoolMXBean permGen =  getPermGen() ;
		MemoryUsage usage = new MemoryUsage(Runtime.getRuntime().maxMemory(), Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory(), permGen.getUsage().getMax(), permGen.getUsage().getUsed());
		return usage;
	}

	private List<MemoryPoolMXBean> getMemoryPoolInformation() {
		List<MemoryPoolMXBean> list = ManagementFactory.getMemoryPoolMXBeans();
		return list;
	}
	
	private MemoryPoolMXBean getPermGen() {
		for (MemoryPoolMXBean mi : getMemoryPoolInformation()) {
		    String name = mi.getName().toLowerCase();
		    if (name.contains("perm gen"))
			return mi;
		}
		return new MemoryPoolMXBean() { 
			
		    public String getName() {
		    	return "";
		    }
 
			public ObjectName getObjectName() { 
				return null;
			}
 
			public MemoryType getType() { 
				return null;
			}
 
			public java.lang.management.MemoryUsage getUsage() { 
				return new java.lang.management.MemoryUsage(0, 0, 0, 0);
			}
 
			public java.lang.management.MemoryUsage getPeakUsage() { 
				return new java.lang.management.MemoryUsage(0, 0, 0, 0);
			}
 
			public void resetPeakUsage() { 
				
			}
 
			public boolean isValid() { 
				return false;
			}
 
			public String[] getMemoryManagerNames() { 
				return null;
			}
 
			public long getUsageThreshold() { 
				return 0;
			}
 
			public void setUsageThreshold(long threshold) { 
				
			}
 
			public boolean isUsageThresholdExceeded() { 
				return false;
			}
 
			public long getUsageThresholdCount() { 
				return 0;
			}
 
			public boolean isUsageThresholdSupported() { 
				return false;
			}
 
			public long getCollectionUsageThreshold() { 
				return 0;
			}
 
			public void setCollectionUsageThreshold(long threshold) { 
				
			}
 
			public boolean isCollectionUsageThresholdExceeded() { 
				return false;
			}
 
			public long getCollectionUsageThresholdCount() { 
				return 0;
			}
 
			public java.lang.management.MemoryUsage getCollectionUsage() { 
				return null;
			}
 
			public boolean isCollectionUsageThresholdSupported() { 
				return false;
			}

		};
	 }
	
	public SystemInfo getSystemInfo() {

		SystemInfo info = new SystemInfo();
		Date now = new Date();

		Locale localeToUse = configService.getLocale(); 
		info.setDate(new SimpleDateFormat("EEEEEE, yyyy MMM dd", localeToUse).format(now));
		info.setTime(new SimpleDateFormat("HH:mm:ss", localeToUse).format(now));
		info.setAvailableProcessors(Runtime.getRuntime().availableProcessors());
		
		Properties sysProps = System.getProperties();
		info.setJavaVendor(sysProps.getProperty("java.vendor"));
		info.setJvmVersion(sysProps.getProperty("java.vm.specification.version"));
		info.setJvmVendor(sysProps.getProperty("java.vm.specification.vendor"));
		info.setJvmImplementationVersion(sysProps.getProperty("java.vm.version"));
		info.setJavaRuntime(sysProps.getProperty("java.runtime.name"));
		info.setJavaVm(sysProps.getProperty("java.vm.name"));
		info.setUserName(sysProps.getProperty("user.name")); 
		info.setSystemLanguage(sysProps.getProperty("user.language"));
		info.setSystemTimezone(sysProps.getProperty("user.timezone")); 
		info.setOperatingSystem((new StringBuilder()).append(sysProps.getProperty("os.name")).append(" ").append(sysProps.getProperty("os.version")).toString());

		info.setOperatingSystemArchitecture(sysProps.getProperty("os.arch"));
		info.setFileSystemEncoding(sysProps.getProperty("file.encoding"));
		info.setJvmInputArguments(getJvmInputArguments());
		info.setWorkingDirectory(sysProps.getProperty("user.dir"));
		info.setTempDirectory(sysProps.getProperty("java.io.tmpdir"));
		
		return info;
	}


	public String getJvmInputArguments() {
		StringBuilder sb = new StringBuilder();
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		for (String arg : runtimeMXBean.getInputArguments()) {
			sb.append(arg).append(" ");
		}
		return sb.toString();
	}



}
