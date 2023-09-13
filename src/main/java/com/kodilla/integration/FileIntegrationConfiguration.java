package com.kodilla.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Configuration
public class FileIntegrationConfiguration {

   @Bean
   FileReadingMessageSource fileAdapter1() {
      FileReadingMessageSource fileSource = new FileReadingMessageSource();
      fileSource.setDirectory(new File("data/input"));
      return fileSource;
   }

   @Bean
   FileReadingMessageSource fileAdapter2() {
      FileReadingMessageSource fileSource = new FileReadingMessageSource();
      fileSource.setDirectory(new File("data/input"));
      return fileSource;
   }

   @Bean
   FileTransformer transformer() {
      return new FileTransformer();
   }

   @Bean
   FileLister lister(){
      return new FileLister();
   }

   @Bean(name = "adapter1")
   FileWritingMessageHandler outputFileAdapter() {
      File directory = new File("data/output");
      FileWritingMessageHandler handler = new FileWritingMessageHandler(directory);
      handler.setExpectReply(false);

      return handler;
   }

   @Bean
   IntegrationFlow fileIntegrationFlow(
           FileReadingMessageSource fileAdapter1,
           FileTransformer transformer,
           FileWritingMessageHandler outputFileHandler) {

      return IntegrationFlow.from(fileAdapter1, config -> config.poller(Pollers.fixedDelay(1000)))
              .transform(transformer, "transformFile")
              .handle(outputFileHandler)
              .get();
   }
   @Bean
   IntegrationFlow fileListFlow(
           FileReadingMessageSource fileAdapter2,
           FileLister fileLister) {

      return IntegrationFlow.from(fileAdapter2, config -> config.poller(Pollers.fixedDelay(1000)))
              .transform(fileLister, "fileDir")
              .handle(m -> System.out.println(m.getPayload()))
              .get();
   }
}