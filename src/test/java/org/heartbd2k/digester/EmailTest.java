package org.heartbd2k.digester;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AK on 9/14/2015.
 */
public class EmailTest {
    @Test
    public void testSend() throws Exception {
        List<String> recipients = new ArrayList<>();
        recipients.add("vincekyi@gmail.com");
        assert Email.send("email.prop", recipients, "test", "test body", null);
    }
}
