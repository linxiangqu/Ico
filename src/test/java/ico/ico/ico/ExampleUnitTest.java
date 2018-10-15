package ico.ico.ico;

import ico.ico.constant.RingTypeEnum;
import ico.ico.util.Common;

import org.junit.Test;

import java.io.File;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

//        System.out.println(RingTypeEnum.ALARM.getIndex() + "");

        String name = Common.getFilename(new File("/dsadas/dasdas/dasdas.pdf"));
        System.out.println(name);
    }

}