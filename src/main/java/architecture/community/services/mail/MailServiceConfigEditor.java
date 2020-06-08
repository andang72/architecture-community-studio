package architecture.community.services.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.springframework.core.io.ClassPathResource;

import architecture.community.util.CommunityConstants;
import architecture.ee.component.editor.AbstractXmlEditor;

public class MailServiceConfigEditor extends AbstractXmlEditor {
	
	org.dom4j.Element element;
	
	private MailServicesConfig config = null;
	
	public MailServiceConfigEditor(File file) {  
		super(file);  
		if(!file.exists()) {
			try {
				ClassPathResource resource = new ClassPathResource(CommunityConstants.SERVICES_CONFIG_FILENAME);
				InputStream input = resource.getInputStream();
				FileUtils.copyInputStreamToFile(input, file); 
				setFile(file);
				initialize(); 
				this.element.addAttribute("name", "MailService");
				this.element.addAttribute("enabled", "false");
				this.element.addElement("javaMailProperties");
				write();
			} catch (IOException e) { 
			}
		}else {
			initialize();
		}
	}
	
	public void createIfNotExist(File file) {
		if(!file.exists()) {
			try {
				ClassPathResource resource = new ClassPathResource(CommunityConstants.SERVICES_CONFIG_FILENAME);
				InputStream input = resource.getInputStream();
				FileUtils.copyInputStreamToFile(input, file); 
				setFile(file);
				initialize(); 
				this.element.addAttribute("name", "MailService");
				this.element.addAttribute("enabled", "false");
				this.element.addElement("javaMailProperties");
				write();
			} catch (IOException e) { 
			}
		}
	}
	
	protected void initialize() { 
		Element root = getDocument().getRootElement();
		this.element = root.element("services");  
	}
	
	public MailServicesConfig getMailServicesConfig() {
		if(config == null)
		{
			config = new MailServicesConfig(); 
			String enabledStr = element.attributeValue("enabled", "false");
			config.setEnabled( Boolean.parseBoolean(enabledStr));
			config.setProtocol(StringUtils.defaultIfEmpty(element.elementTextTrim("protocol"), null ));
			config.setSsl(Boolean.parseBoolean(StringUtils.defaultIfEmpty(element.elementTextTrim("ssl"), "false")));
			config.setHost(StringUtils.defaultIfEmpty(element.elementTextTrim("host"), null ));
			config.setPort(Integer.parseInt(StringUtils.defaultIfEmpty(element.elementTextTrim("port"), "0")));
			config.setUsername(StringUtils.defaultIfEmpty(element.elementTextTrim("username"), null ));
			config.setPassword(StringUtils.defaultIfEmpty(element.elementTextTrim("password"), null ));
			config.setDefaultEncoding(StringUtils.defaultIfEmpty(element.elementTextTrim("defaultEncoding"), "UTF-8" )); 
			if( element.element("javaMailProperties") != null ) {
				for( Element ele : element.element("javaMailProperties").elements("property") ) {
					config.getProperties().put(ele.attributeValue("name"), ele.getTextTrim()); 
				}
			}
		} 
		return config;
	}
	
	public void setMailServicesConfig(MailServicesConfig config) {
		this.config = config;
		element.addAttribute("enabled", config.getEnabled().toString());
		getElementAndCreateIfNotExist("protocol").setText(StringUtils.defaultString(config.getProtocol(), ""));
		getElementAndCreateIfNotExist("username").setText(StringUtils.defaultString(config.getUsername(), ""));
		getElementAndCreateIfNotExist("password").setText(StringUtils.defaultString(config.getPassword(), ""));
		getElementAndCreateIfNotExist("host").setText(StringUtils.defaultString(config.getHost(), ""));
		getElementAndCreateIfNotExist("port").setText(StringUtils.defaultString(config.getPort().toString(), "0"));
		getElementAndCreateIfNotExist("defaultEncoding").setText(StringUtils.defaultString(config.getDefaultEncoding(), "UTF-8"));
		
		if( element.element("javaMailProperties") != null )
			element.remove(element.element("javaMailProperties"));
		
		Element props = element.addElement("javaMailProperties");
		for( String key : config.getProperties().keySet()){
			String value = config.getProperties().get(key);
			Element ele2 = props.addElement("property");
			ele2.addAttribute("name", key );
			ele2.setText(value);
		}
		write();
	}	
	
	private Element getElementAndCreateIfNotExist(String name) {
		Element ele = element.element(name);
		if( ele == null )
			ele = element.addElement(name);
		return ele;
	}
	
}
