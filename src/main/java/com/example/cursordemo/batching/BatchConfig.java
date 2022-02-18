package com.example.cursordemo.batching;

import com.example.cursordemo.batching.UserFieldMapper;
import com.example.cursordemo.models.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final String[] TOKENS = {"id", "username"};

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get("job")
                .flow(step)
                .end().build();
    }

    @Bean
    public Step step(ItemReader<User> itemReader, ItemWriter<User> jsonUserwritter) {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(3)
                .reader(itemReader)
                .processor(userUserItemProcessor())
                .writer(jsonUserwritter)
                .build();


    }

    @Bean
    @StepScope
    public FlatFileItemReader<User> csvItemReader(@Value("#{jobParameters['file.input']}") String input) {
        var flatFileItemReaderBuilder = new FlatFileItemReaderBuilder<User>();
        var userFieldMapper = new UserFieldMapper();

        return flatFileItemReaderBuilder.
                name("userReader")
                .resource(new FileSystemResource(input))
                .delimited()
                .names(TOKENS)
                .fieldSetMapper(userFieldMapper)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<User, User> userUserItemProcessor() {
        return user -> {
            user.setUsername("!!!!! " + user.getUsername());
            return user;
        };
    }

    @Bean
    @StepScope
    public JsonFileItemWriter<User> jsonFileItemWriter(@Value("#{jobParameters['file.output']}") String output) {

        var builder = new JsonFileItemWriterBuilder<User>();
        var jsonObjectMarshaller = new JacksonJsonObjectMarshaller<User>();

        return builder
                .name("userWritter")
                .jsonObjectMarshaller(jsonObjectMarshaller)
                .resource(new FileSystemResource(output))
                .build();

    }
}
