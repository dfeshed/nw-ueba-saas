package fortscale.monitor;

import static org.mockito.Mockito.*;
import junitparams.Parameters;
import junitparams.JUnitParamsRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.monitor.domain.JobDataReceived;

@RunWith(JUnitParamsRunner.class)
public class JobProgressReportShellCommandTest {

	private JobProgressReportShellCommand subject;
	private JobProgressReporter reporter;
	
	@Before
	public void setUp() throws Exception {
		reporter = mock(JobProgressReporter.class);
		subject = new JobProgressReportShellCommand(reporter);
	}
	
	
	@Test
	public void run_with_no_arguments_should_do_nothing() {
		subject.run(null);
		
		verifyZeroInteractions(reporter);
	}
	
	@Test
	public void run_with_invalid_arguments_should_do_nothing() {
		subject.run(new String[] { "sss", "xcge" });
		
		verifyZeroInteractions(reporter);
	}
	
	
	@Test
	@Parameters({ "sss", 
				  "-sj sdf",
				  "",
				  "-fj",
				  "-ss xx ",
				  "-ss xx xx ",
				  "-fs xx",
				  "-data id general xxx MB",
				  "-data id general 3",
				  "-data id general",
				  "-data id",
				  "-data"
				})
	public void run_with_invalid_command_should_do_nothing(String argstr) {
 		String[] args = argstr.split(" ");
 		
 		subject.run(args);
 		
		verifyZeroInteractions(reporter);
	}
	
	
	@Test
	public void run_with_sj_should_call_reporter_with_arguments() {
		subject.run(new String[] { "-sj", "VPN", "Fetch", "0", "true" });
		
		verify(reporter).startJob("VPN", "Fetch", 0, true);
	}
	
	@Test
	public void run_with_fj_should_call_reporter_with_arguments() {
		subject.run(new String[] { "-fj", "JobID" });
		
		verify(reporter).finishJob("JobID");
	}
	
	@Test
	public void run_with_ss_should_call_reporter_with_arguments() {
		subject.run(new String[] { "-ss", "JobID", "StepA", "1" });
		
		verify(reporter).startStep("JobID", "StepA", 1);
	}
	
	@Test
	public void run_with_fs_should_call_reporter_with_arguments() {
		subject.run(new String[] { "-fs", "JobID", "StepA" });
		
		verify(reporter).finishStep("JobID", "StepA");
	}
	
	
	@Test
	public void run_with_err_should_pass_all_message_parts_seperated_by_space_to_reporter() {
		subject.run(new String[] { "-err", "JobID", "StepA", "part1", "part2", "part3" });
		verify(reporter).error("JobID", "StepA", "part1 part2 part3");
	}
	
	@Test
	public void run_with_warn_should_pass_all_message_parts_seperated_by_space_to_reporter() {
		subject.run(new String[] { "-warn", "JobID", "StepA", "part1", "part2", "part3" });
		verify(reporter).warn("JobID", "StepA", "part1 part2 part3");
	}
	
	@Test
	public void run_with_data_should_pass_values_to_reporter() {
		subject.run(new String[] { "-data", "jobid", "general", "33", "MB" });
		verify(reporter).addDataReceived("jobid", new JobDataReceived("general", new Integer(33), "MB"));
	}
}
