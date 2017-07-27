package org.elifesciences.schematron.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Configuration for the message resource bundles used by this application.
 */
@Configuration
public class MessageConfiguration {

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.addBasenames("classpath:locale/messages");
		messageSource.setDefaultEncoding("UTF-8");

		return messageSource;
	}

}
