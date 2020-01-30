package hdli.chapter7;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MsgPackDemo {

    public static void main(String[] args) throws IOException {
        List<String> src = new ArrayList<String>();
        src.add("msgpack");
        src.add("kumofs");
        src.add("viver");

        MessagePack msgPack = new MessagePack();
        byte[] raw = msgPack.write(src);
        List<String> dest1 = msgPack.read(raw, Templates.tList(Templates.TString));
        System.out.println("dest1.get(0) = " + dest1.get(0));
        System.out.println("dest1.get(1) = " + dest1.get(1));
        System.out.println("dest1.get(2) = " + dest1.get(2));
    }
}
