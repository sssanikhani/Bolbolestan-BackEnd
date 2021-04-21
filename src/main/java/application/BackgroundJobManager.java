package application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BackgroundJobManager implements ApplicationRunner {

	private ScheduledExecutorService scheduler;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new SubmitWaitingListTask(), 0, 15, TimeUnit.MINUTES);
	}
}
