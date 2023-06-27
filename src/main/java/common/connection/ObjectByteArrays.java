package common.connection;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.min;

public class ObjectByteArrays {
    public static int packageSize = 512;

    private int index = 0;
    private final byte[] length;
    private final List<byte[]> data = new ArrayList<>();

    private ObjectByteArrays(byte[] serialized) {
        length = new byte[4];
        ByteBuffer.wrap(length).putInt(serialized.length);

        int i = 0;
        while (i < serialized.length) {
            int j = min(i + packageSize, serialized.length);
            var length = j - i;

            var nextData = new byte[length];
            System.arraycopy(serialized, i, nextData, 0, length);
            data.add(nextData);
            i = j;
        }
    }

    public static ObjectByteArrays getArrays(Serializable obj) {
        var b = new ByteArrayOutputStream();
        try {
            ObjectOutputStream ObjOut = new ObjectOutputStream(b);
            ObjOut.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ObjectByteArrays(b.toByteArray());
    }

    public static ObjectByteArrays getEmpty(int sz) {
        var obj = new ObjectByteArrays(new byte[sz]);
        obj.index = 1;
        return obj;
    }

    public byte[] getNext() {
        if (index == 0) {
            index++;
            return length;
        }
        if (index > data.size()) return new byte[0];
        var res = data.get(index - 1);
        index++;
        return res;
    }

    public Object toObject() {
        index = 0;
        byte[] serialized = new byte[ByteBuffer.wrap(this.getNext()).getInt()];
        byte[] pack = this.getNext();
        int i = 0;
        while (pack.length != 0) {
            int j = min(i + packageSize, serialized.length);
            var length = j - i;
            System.arraycopy(pack, 0, serialized, i, length);
            i = j;
            pack = this.getNext();
        }
        index = 0;
        try (ObjectInput ObjIn = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
            return ObjIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int len() {
        return data.size();
    }

    public boolean addNext(byte[] arr) {
        assert index <= data.size();
        assert index >= 1;
        var p = data.get(index - 1);
        assert p.length == arr.length;
        System.arraycopy(arr, 0, p, 0, arr.length);
        index++;
        return index <= data.size();
    }

    public void iterBack() {
        assert index > 0;
        index--;
    }

    public boolean hasNext() {
        return index <= data.size();
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append(Arrays.toString(length));
        data.forEach((arr) -> s.append(Arrays.toString(arr)));
        return s.toString();
    }
}
