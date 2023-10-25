/*
 * Copyright (c) 2023, Starmoon1617 and/or Nathan Liao. All rights reserved.
 *
 */
package io.github.starmoon1617.starmie.boot.banner.listener;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * Listener to print banner after application start up
 * 
 * @date 2023-10-24
 * @author Nathan Liao
 */
@Order
public class StartUpBannerListener implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * default Banner
     */
    private static final String[] BANNER = { "           _                       _                ___    _  __  ",
            "   ___    | |_    __ _      _ _   | |_      o O O  / _ \\  | |/ /  ", "  (_-<    |  _|  / _` |    | '_|  |  _|    o      | (_) | | ' <   ",
            "  /__/_   _\\__|  \\__,_|   _|_|_   _\\__|   TS__[O]  \\___/  |_|\\_\\  ",
            "_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"| {======|_|\"\"\"\"\"|_|\"\"\"\"\"| ",
            "\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'./o--000'\"`-0-0-'\"`-0-0-' " };

    /**
     * Banner location
     */
    public static final String BANNER_LOCATION = "starmie.boot.startup.banner";

    /**
     * default Banner name
     */
    public static final String DEFAULT_BANNER_LOCATION = "starmieBanner.txt";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        Banner banner = getTextBanner(applicationContext);
        if (banner != null) {
            String string = null;
            try {
                string = createStringFromBanner(banner, applicationContext.getEnvironment(), getClass());
            } catch (Exception e) {
                string = null;
            }
            if (StringUtils.hasText(string)) {
                System.out.println(string);
                return;
            }
        }
        for (String line : BANNER) {
            System.out.println(line);
        }
    }

    /**
     * get Banner With configuration from applicationContext
     * 
     * @param applicationContext
     * @return
     */
    private Banner getTextBanner(ConfigurableApplicationContext applicationContext) {
        String location = applicationContext.getEnvironment().getProperty(BANNER_LOCATION, DEFAULT_BANNER_LOCATION);
        Resource resource = applicationContext.getResource(location);
        try {
            if (!resource.exists()) {
                resource = applicationContext.getResource("classpath:" + location);
            }
            if (resource.exists() && !resource.getURL().toExternalForm().contains("liquibase-core")) {
                return new ResourceBanner(resource);
            }
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * Transfer Banner to String
     * 
     * @param banner
     * @param environment
     * @param mainApplicationClass
     * @return
     * @throws Exception
     */
    private String createStringFromBanner(Banner banner, Environment environment, Class<?> mainApplicationClass) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        banner.printBanner(environment, mainApplicationClass, new PrintStream(baos));
        String charset = environment.getProperty("spring.banner.charset", "UTF-8");
        return baos.toString(charset);
    }

}
