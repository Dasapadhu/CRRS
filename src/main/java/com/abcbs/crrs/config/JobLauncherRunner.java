package com.abcbs.crrs.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JobLauncherRunner implements CommandLineRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job p09373Job;
	
	@Autowired
	@Qualifier("p09390Job")  // ✅ Match the bean name from your Config class
	private Job p09390Job;
	
	@Autowired
	@Qualifier("p09352Job")   // must match @Bean name in P09352Config
	private Job p09352Job;
	
	@Autowired
	@Qualifier("p09365Job")   // must match @Bean name in P09352Config
	private Job p09365Job;
	
	@Autowired
	@Qualifier("p09175Job")
	private Job p09175Job;
	
	@Autowired
	@Qualifier("p09370Job")  
	private Job p09370Job;
	
	private static final Logger logger = LogManager.getLogger(JobLauncherRunner.class);

	@Override
	public void run(String... args) throws Exception {
		logger.info("Entered into run");
		JobExecution jobExecution;
		String jobName;

		if (args.length > 0) {
			jobName = args[0];
			if ("P09373".equals(jobName)) {
				logger.info("Requested job: P09373");

				if (args.length < 2) {
					logger.error("P09373 requires parameters: inputFile");
					throw new IllegalArgumentException("Missing parameters for P09373");
				}

				String inputFile = args[1];

				JobParameters jobParameters = new JobParametersBuilder().addString("inputFile", inputFile)
						.addLong("run.id", System.currentTimeMillis()).toJobParameters();

				jobExecution = jobLauncher.run(p09373Job, jobParameters);
				logger.info("Job {} finished with status: {}", jobName, jobExecution.getStatus());

			} else if ("P09390".equals(jobName)) {
                logger.info("Requested for P09390");

                if (args.length >= 4) {

                    String newVendorFile = args[1];
                    String fepmanFile    = args[2];
                    String outputFile    = args[3];

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("newVendorFile", newVendorFile)
                            .addString("fepmanFile", fepmanFile)
                            .addString("outputFepVendor", outputFile)
                            .addLong("run.id", System.currentTimeMillis())
                            .toJobParameters();

                    jobExecution = jobLauncher.run(p09390Job, jobParameters);

                } else {
                    logger.error("Insufficient number of parameters for P09390");
                    throw new IllegalArgumentException("INSUFFICIENT NO. OF PARAMS FOR P09390");
                }
			} else if ("P09352".equals(jobName)) {
			    logger.info("Requested for P09352");
			    if (args.length >= 11) {

			        String jobType = args[1];
			        String datePicker = args[2];
			        String corpCardFile    = args[3];
			        String checkpointFile  = args[4];
			        String ivoucherFile    = args[5];

			        String xapntrfcPath    = args[6];
			        String xp09Path        = args[7];
			        String xp07Path        = args[8];
			        String xvoucherPath    = args[9];
			        String reportPath      = args[10];
			        JobParameters jobParameters = new JobParametersBuilder()
			                .addString("jobType", jobType)
			                .addString("datePicker", datePicker)
			                .addString("corpCardFile", corpCardFile)
			                .addString("checkpointFile", checkpointFile)
			                .addString("ivoucherFile", ivoucherFile)
			                .addString("xapntrfcPath", xapntrfcPath)
			                .addString("xp09Path", xp09Path)
			                .addString("xp07Path", xp07Path)
			                .addString("xvoucherPath", xvoucherPath)
			                .addString("reportPath", reportPath)
			                .addLong("run.id", System.currentTimeMillis())
			                .toJobParameters();

			        jobExecution = jobLauncher.run(p09352Job, jobParameters);
			        logger.info("Job {} finished with status: {}", jobName, jobExecution.getStatus());

			    } else {
			        logger.error("Insufficient number of parameters for P09352");
			        throw new IllegalArgumentException(
			                "P09352 requires parameters: controlCardFile corpCardFile checkpointFile ivoucherFile xapntrfcPath xp09Path xp07Path xvoucherPath reportPath");
			    }
			} else if ("P09365".equals(jobName)) {
                logger.info("Requested for P09365");

                if (args.length >= 3) {

                    String inputFile   = args[1];
                    String outputFile  = args[2];

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("inputFile", inputFile)
                            .addString("outputFile", outputFile)
                            .addLong("run.id", System.currentTimeMillis())
                            .toJobParameters();

                    jobExecution = jobLauncher.run(p09365Job, jobParameters);

                } else {
                    logger.error("Insufficient number of parameters for P09365");
                    throw new IllegalArgumentException("INSUFFICIENT NO. OF PARAMS FOR P09365");
                }
			} else if ("P09175".equals(jobName)) {
                logger.info("Requested for P09175");

                if (args.length >= 4) {

					String P09175_CcmOutput = args[1];
					String P09175_ControlTotal = args[2];
					String P09175_ReportOutput = args[3];
                    
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("P09175_ReportOutput", P09175_ReportOutput)
                            .addString("P09175_CcmOutput", P09175_CcmOutput)
                            .addString("P09175_ControlTotal", P09175_ControlTotal)
                            .addLong("run.id", System.currentTimeMillis())
                            .toJobParameters();

                    jobExecution = jobLauncher.run(p09175Job, jobParameters);

                } else {
                    logger.error("Insufficient number of parameters for P09175");
                    throw new IllegalArgumentException("INSUFFICIENT NO. OF PARAMS FOR P09175");
                }
			}else if ("P09370".equals(jobName)) {
                logger.info("Requested for P09370");
                if (args.length >= 3) {
                    String param1 = args[1];
                    String param2 = args[2];
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("inputFile", param1)
                            .addString("outputFile", param2)
                            .addLong("run.id", System.currentTimeMillis())
                            .toJobParameters();
                    jobExecution = jobLauncher.run(p09370Job, jobParameters);
                } else {
                    logger.error("Insufficient number of parameters");
                    throw new IllegalArgumentException("INSUFFICIENT NO. OF PARAMS");
                }
            } else {
				logger.error("Job has not found");
				throw new IllegalArgumentException("JOB NOT FOUND");
			}
		}
	}
}
