package cn.mingyuan.pullword;

import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

/**
 * @author jiangmingyuan@myhaowai.com
 * @version 2016/12/30 12:29
 * @since jdk1.8
 */
public class PullwordTest {

    @Test
    public void test() throws IOException {
        String hello = Pullword.getPlainText("hello");
        System.out.println(hello);

        Collection<Pullword.Result> results = Pullword.get("hello world");
        System.out.println(results.toString());
    }

    public static void main(String[] args) throws IOException {
        new PullwordTest().test();
    }
}
