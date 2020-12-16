package com.Assignment.VotingClient.config;


import com.Assignment.VotingClient.models.VotingInfo;
import com.Assignment.VotingClient.writers.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@EnableBatchProcessing
public class BatchConfig
{
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job readCSVFilesJob() {
        return jobBuilderFactory
                .get("readCSVFilesJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<VotingInfo, VotingInfo>chunk(5)
                .reader(multiResourceItemReader())
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<VotingInfo> multiResourceItemReader()
    {
        Resource[] inputResources = null;
        FileSystemXmlApplicationContext patternResolver = new FileSystemXmlApplicationContext();
        try {
            inputResources = patternResolver.getResources("input/votingRecordFile*.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultiResourceItemReader<VotingInfo> resourceItemReader = new MultiResourceItemReader<VotingInfo>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FlatFileItemReader<VotingInfo> reader()
    {

        //Create reader instance
        FlatFileItemReader<VotingInfo> reader = new FlatFileItemReader<VotingInfo>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(";");
        delimitedLineTokenizer.setNames(new String[] {"voter_name","bo0th_name","party"});
        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(0);

        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper() {
            {
                //3 columns in each row
                setLineTokenizer(delimitedLineTokenizer);
                //Set values in Voting class
                setFieldSetMapper(new BeanWrapperFieldSetMapper<VotingInfo>() {
                    {
                        setTargetType(VotingInfo.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    public ConsoleItemWriter<VotingInfo> writer()
    {
        return new ConsoleItemWriter<VotingInfo>();
    }
}
