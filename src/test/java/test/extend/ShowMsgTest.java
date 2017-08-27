package test.extend;

import avalon.extend.Scheduler;
import avalon.extend.ShowMsg;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ShowMsgTest {
	public static void main(String[] args) {
		new ShowMsg();
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		executor.scheduleAtFixedRate(new Scheduler(), 1, 5, TimeUnit.SECONDS);
	}
}
