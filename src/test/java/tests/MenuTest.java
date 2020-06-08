package tests;

import java.io.StringWriter;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import architecture.community.admin.menu.MenuComponent;
import architecture.community.admin.menu.SetupMenuService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { 
		"classpath:application-community-context.xml",	
		"classpath:context/community-user-context.xml",
		"classpath:context/community-setup-context.xml",
		"classpath:context/community-utils-context.xml",
		"classpath:context/community-core-context.xml"})
public class MenuTest {

	private static final Logger logger = LoggerFactory.getLogger(MenuTest.class);
	
	@Autowired(required=false)
	private SetupMenuService setupMenuService;
	
	public MenuTest() { 
	}

	
	@Test
	public void getMenu() {
		if(setupMenuService!=null) {
			Set<String> set =  setupMenuService.getMenuNames();
			logger.debug( "MenuNames: {}" , set);
		}
	}

	public void createMenuXml() {
		try {
			// First create Stax components we need
			XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
			StringWriter out = new StringWriter();
			XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(out);
			
			// then Jackson components.
			XmlMapper mapper = new XmlMapper(xmlInputFactory);
			
			sw.writeStartDocument();
			sw.writeStartElement("MenuConfig");

			// Write whatever content POJOs...
			MenuComponent mc = new MenuComponent();
			mapper.writeValue(sw, mc);
			// and/or regular Stax output
			sw.writeComment("Some insightful commentary here");
			sw.writeEndElement();
			sw.writeEndDocument();
			
			logger.debug(out.toString());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
