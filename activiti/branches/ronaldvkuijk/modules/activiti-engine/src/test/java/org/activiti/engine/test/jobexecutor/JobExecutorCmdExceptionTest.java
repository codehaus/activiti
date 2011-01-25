/**
 * 
 */
package org.activiti.engine.test.jobexecutor;

import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.cmd.DeleteJobsCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.runtime.MessageEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.impl.util.ClockUtil;

/**
 * @author Tom Baeyens
 */
public class JobExecutorCmdExceptionTest extends PluggableActivitiTestCase {

  protected TweetExceptionHandler tweetExceptionHandler = new TweetExceptionHandler();

  private CommandExecutor commandExecutor;
  private JobExecutor jobExecutor;

  public void setUp() throws Exception {
    processEngineConfiguration.getJobHandlers().put(tweetExceptionHandler.getType(), tweetExceptionHandler);
    this.commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
  }

  public void tearDown() throws Exception {
    processEngineConfiguration.getJobHandlers().remove(tweetExceptionHandler.getType());
  }

  public void testJobCommandsWith2Exceptions() {
    
    // Create but do not start. Will be done in waitFor... we just need the jobExecutor lockowner here
    jobExecutor = processEngineConfiguration.getJobExecutor();
    jobExecutor.hasAcquisitionPerQueue(true);
    
    commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        MessageEntity message = createTweetExceptionMessage();
        commandContext.getMessageSession().send(message);
        return message.getId();
      }
    });

    waitForJobExecutorToProcessAllJobs(20000L, 500L);
  }

  public void testJobCommandsWith3Exceptions() {
    
    // Create but do not start. Will be done in waitFor... we just need the jobExecutor lockowner here
    // A new one since the previous test has shut it down!!!! maybe new convenince method??? 
    //JobExecutor jobExecutor = new JobExecutor();
    JobExecutor jobExecutor = ((ProcessEngineImpl)processEngine).getJobExecutor();
    jobExecutor.hasAcquisitionPerQueue(true);

    tweetExceptionHandler.setExceptionsRemaining(3);

    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        MessageEntity message = createTweetExceptionMessage();
        commandContext.getMessageSession().send(message);
        return message.getId();
      }
    });

    waitForJobExecutorToProcessAllJobs(20000L, 500L);

    // check if there is a failed job in the DLQ (total over all queues)
    assertEquals(1, processEngine.getManagementService().createJobQuery().dlq().count());

    commandExecutor.execute(new DeleteJobsCmd(jobId));
  }

  protected MessageEntity createTweetExceptionMessage() {
    MessageEntity message = new MessageEntity();
    message.setJobHandlerType("tweet-exception");
    return message;
  }
}
