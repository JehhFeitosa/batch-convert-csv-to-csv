package com.feitosa.batchconvertcsvtocsv.configuration;

import com.feitosa.batchconvertcsvtocsv.model.Student;
import com.feitosa.batchconvertcsvtocsv.processor.StudentProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class StudentBatchConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public FlatFileItemReader<Student> readDataFromCsv(){
        FlatFileItemReader<Student> reader = new FlatFileItemReader<Student>();
        reader.setResource(new FileSystemResource("C:\\archive-poc-batch\\csv_input.csv"));
        reader.setLineMapper(new DefaultLineMapper<Student>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(Student.fields());
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>(){
                    {
                        setTargetType(Student.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    public StudentProcessor processor(){
        return new StudentProcessor();
    }

    @Bean
    public FlatFileItemWriter<Student> writer(){
        FlatFileItemWriter<Student> writer = new FlatFileItemWriter<Student>();
        writer.setResource(new FileSystemResource("C:\\archive-poc-batch\\csv_output.csv"));
        DelimitedLineAggregator<Student> aggregator = new DelimitedLineAggregator<>();
        BeanWrapperFieldExtractor<Student> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(Student.fields());
        aggregator.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(aggregator);
        return writer;
    }

    @Bean
    public Step executeStudentStep(){
        return stepBuilderFactory.get("executeStudentStep").<Student, Student>chunk(5)
        .reader(readDataFromCsv()).processor(processor()).writer(writer()).build();
    }

    @Bean
    public Job processStudentJob(){
        return jobBuilderFactory.get("processStudentJob").flow(executeStudentStep()).end().build();
    }

}
