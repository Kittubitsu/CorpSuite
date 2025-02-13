//package toshu.org.corpsuite.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import toshu.org.corpsuite.security.SessionInterceptor;
//
//@Configuration
//public class WebConfiguration implements WebMvcConfigurer {
//
//
//    private SessionInterceptor sessionInterceptor;
//
//    public WebConfiguration(SessionInterceptor sessionInterceptor) {
//        this.sessionInterceptor = sessionInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//
//        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**").excludePathPatterns("/css/**", "/images/**", "/js/**");
//    }
//}
