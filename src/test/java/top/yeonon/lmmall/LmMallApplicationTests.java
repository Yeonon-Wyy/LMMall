package top.yeonon.lmmall;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LmMallApplicationTests {

	@Test
	public void contextLoads() {

	}

	@Test
	public void testDate() {
		Date date = new Date();
		Date orderDate = DateUtils.addHours(date,-2);
		System.out.println();
	}

}
