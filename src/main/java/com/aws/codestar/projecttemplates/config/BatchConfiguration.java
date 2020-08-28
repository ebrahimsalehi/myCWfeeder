package com.aws.codestar.projecttemplates.config;

import com.aws.codestar.projecttemplates.batch.PersonItemProcessor;
import com.aws.codestar.projecttemplates.entity.Person;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean("flatFileItemReader")
    public FlatFileItemReader<Person> flatFileItemReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    @Qualifier("jdbcCursorItemReader")
    public ItemReader<Person> jdbcCursorItemReader() {
        JdbcCursorItemReader<Person> result = new JdbcCursorItemReader<>();
        result.setDataSource(dataSource);
        result.setSql("select ID, source_text from source");
        result.setIgnoreWarnings(true);
        result.setVerifyCursorPosition(true);

        result.setRowMapper(new RowMapper<Person>() {
            @Override
            public Person mapRow(ResultSet rs, int i) throws SQLException {
                Person person = new Person();
                person.setFirstName("SourceID:" + rs.getLong("ID"));
                return person;
            }
        });
        result.setFetchSize(10);
        result.setMaxRows(100);
        result.setQueryTimeout(30000);
        result.setSaveState(true);
        return result;    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(10)
                .reader(jdbcCursorItemReader())
                .processor(processor())
                .writer(writer)
                .build();
    }

}