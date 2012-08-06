package domparser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.gnim.extend.DomParser;
import net.gnim.extend.DomParser.NodeElement;

/**
 *
 * @author ming
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        DomParser dom = DomParser.create(readXml());
        ArrayList<NodeElement> students = dom.document.search("student");
        for (int i = 0; i < students.size(); i++) {
            NodeElement node = students.get(i);
            echo("id:" + node.getAttrValue("id"));
            echo("name:" + node.getChildElements("name").get(0).inner());
            echo("age:" + node.getChildElements("age").get(0).inner());
        }
    }

    public static String readXml() throws IOException {
        InputStreamReader reader = new InputStreamReader(Main.class.getResourceAsStream("test.xml"));
        try {
            char[] buff = new char[1024];
            StringBuilder sb = new StringBuilder();
            int count;
            while ((count = reader.read(buff)) != -1) {
                sb.append(buff, 0, count);
            }
            return sb.toString();
        } finally {
            reader.close();
        }
    }

    public static void echo(String msg) {
        System.out.println(msg);
    }
}
