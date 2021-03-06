package com.mazdah.eeimporter.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazdah.eeimporter.config.HttpInterceptor;



@EnableWebMvc
@ComponentScan(basePackages = {"com.mazdah.eeimporter"})
@Configuration
@PropertySource(value = {"classpath:common.properties"})
public class WebMvcConfig implements WebMvcConfigurer {

	Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

	@Value("${file.temp.dir}")
	private String tempDir;
//	private String tempDir = "/Volumes/MacintoshHD2/elastic_data/temp";
	
	@Value("${max.upload.size}")
	private int maxUploadSize;
//	private int maxUploadSize = 1024 * 1024 * 1024;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		registry.addInterceptor(httpInterceptor())
				.addPathPatterns("/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/js/**").addResourceLocations("/resources/js/");
		registry.addResourceHandler("/css/**").addResourceLocations("/resources/css/");
		registry.addResourceHandler("/images/**").addResourceLocations("/resources/images/");
		registry.addResourceHandler("/png/**").addResourceLocations("/resources/png/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("/resources/fonts/");
		registry.addResourceHandler("/html/**").addResourceLocations("/resources/html/");
		registry.addResourceHandler("/less/**").addResourceLocations("/resources/less/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// TODO Auto-generated method stub
		
		registry.addViewController("/EEImporter/").setViewName("index");
		registry.addViewController("/EEImporter/admin/").setViewName("index");
		registry.addViewController("/").setViewName("index");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		// TODO Auto-generated method stub
		
		configurer.enable();
	}
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		// TODO Auto-generated method stub
		configurer.favorPathExtension(false).
	    favorParameter(true).
	    parameterName("mediaType").
	    ignoreAcceptHeader(true).
	    defaultContentType(MediaType.APPLICATION_JSON).
	    mediaType("xml", MediaType.APPLICATION_XML). 
	    mediaType("json", MediaType.APPLICATION_JSON); 
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		// TODO Auto-generated method stub
		converters.add(jsonConverter());
	    converters.add(xmlConverter());
	}

	@Bean
	public ViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".html");
		
		return resolver;
	}
	
//	@Bean
//	public MultipartConfigElement multipartConfigElement() {
//		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(tempDir, 
//				maxUploadSize, maxUploadSize * 2, maxUploadSize / 2);
//		return multipartConfigElement;
//	}
	
//	@Bean(name = "multipartResolver")
//	public CommonsMultipartResolver multipartResolver() {
//		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
//		resolver.setDefaultEncoding("utf-8");
//		resolver.setMaxUploadSize(1024^4);
//		resolver.setMaxUploadSizePerFile(1024^3);
//		
//		try {
//			logger.debug("##### tempDir = " + tempDir);
//			resolver.setUploadTempDir(new FileSystemResource(tempDir));
//		} catch (IOException ioe) {
//			logger.debug("IOException : " + ioe.getLocalizedMessage());
//		}
//		
//		return resolver;
//	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceHolderConfigurer() {
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		pspc.setIgnoreUnresolvablePlaceholders(true);
		
		return pspc;
	}
	
	@Bean
	public HttpInterceptor httpInterceptor() {
		return new HttpInterceptor();
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter jsonConverter() {
	    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
	    ObjectMapper objectMapper = new ObjectMapper();
	    jsonConverter.setObjectMapper(objectMapper);
	    return jsonConverter;
	}

	@Bean
	public MappingJackson2XmlHttpMessageConverter xmlConverter() {
	    MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter();
	    return xmlConverter;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCorsMappings(CorsRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFormatters(FormatterRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MessageCodesResolver getMessageCodesResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Validator getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
