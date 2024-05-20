package com.sbl.demo.sblproject.common.config;

import com.sbl.demo.sblproject.common.config.interceptor.CommonInterceptor;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CommonInterceptor commonInterceptor;
    private final String FILE_ROOT_URI;
    private final String FILE_ROOT_DIR;

    public WebConfig(CommonInterceptor commonInterceptor,
        @Value("${common.file.root_uri}") String rootUri,
        @Value("${common.file.root_dir}") String rootDir) {
        this.commonInterceptor = commonInterceptor;
        this.FILE_ROOT_DIR = rootDir;
        this.FILE_ROOT_URI = rootUri;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("Lang");
        resolver.setDefaultLocale(Locale.getDefault());
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    ReloadableResourceBundleMessageSource bundleMessageSource() {
        ReloadableResourceBundleMessageSource rrbms = new ReloadableResourceBundleMessageSource();
        rrbms.setBasename("classpath:i18n/messages");
        rrbms.setDefaultEncoding("UTF-8");
        rrbms.setCacheSeconds(60);
        rrbms.setUseCodeAsDefaultMessage(true);
        return rrbms;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(false);
        registry.setOrder(1);
        registry.addResourceHandler(FILE_ROOT_URI + "/**")
            .addResourceLocations("file:///" + FILE_ROOT_DIR + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(commonInterceptor);
        registry.addInterceptor(localeChangeInterceptor());
    }


}
