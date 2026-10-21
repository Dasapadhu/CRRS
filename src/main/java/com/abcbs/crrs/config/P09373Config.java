package com.abcbs.crrs.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.abcbs.crrs.jobs.P09373.P09373InputRecord;

@Configuration
public class P09373Config {

    private static final Logger log = LogManager.getLogger(P09373Config.class);

    // ============================================================
    // JOB — MUST USE QUALIFIER
    // ============================================================
    @Bean
    public Job p09373Job(
            JobRepository jobRepository,
            @Qualifier("p09373Step") Step p09373Step) {   // <-- FIXED

        log.info("Configuring Job: P09373Job");

        return new JobBuilder("P09373Job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(p09373Step)
                .build();
    }

    // ============================================================
    // STEP — bean name MUST match @Qualifier in job method
    // ============================================================
    @Bean(name = "p09373Step")   // <-- EXPLICIT BEAN NAME (safe)
    public Step p09373Step(
            JobRepository jobRepository,
            PlatformTransactionManager txManager,
            FlatFileItemReader<P09373InputRecord> p09373Reader,
            ItemWriter<P09373InputRecord> p09373Writer) {

        log.info("Configuring Step: P09373Step with chunk size = 2000");

        return new StepBuilder("P09373Step", jobRepository)
                .<P09373InputRecord, P09373InputRecord>chunk(2000, txManager)
                .reader(p09373Reader)
                .writer(p09373Writer)
                .build();
    }

    // ============================================================
    // READER
    // ============================================================
    @Bean
    @StepScope
    public FlatFileItemReader<P09373InputRecord> p09373Reader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {

        log.info("Initializing FlatFileItemReader for inputFile: {}", inputFile);

        try {
            FixedLengthTokenizer tok = new FixedLengthTokenizer();
            tok.setColumns(
                    new Range(1, 3),   // refundType
                    new Range(4, 6),   // batchPrefix
                    new Range(7, 12),  // batchDate
                    new Range(13, 14)  // batchSuffix
            );
            tok.setNames("refundType", "batchPrefix", "batchDate", "batchSuffix");

            DefaultLineMapper<P09373InputRecord> lm = new DefaultLineMapper<>();
            lm.setLineTokenizer(tok);

            BeanWrapperFieldSetMapper<P09373InputRecord> mapper =
                    new BeanWrapperFieldSetMapper<>();
            mapper.setTargetType(P09373InputRecord.class);

            lm.setFieldSetMapper(mapper);

            FlatFileItemReader<P09373InputRecord> reader =
                    new FlatFileItemReader<>();
            reader.setResource(new FileSystemResource(inputFile));
            reader.setLineMapper(lm);
            reader.setStrict(true);

            log.debug("FlatFileItemReader configured for file: {}", inputFile);

            return reader;

        } catch (Exception e) {
            log.error("Error configuring FlatFileItemReader for {}", inputFile, e);
            throw new IllegalStateException("Failed to configure reader for P09373", e);
        }
    }
}