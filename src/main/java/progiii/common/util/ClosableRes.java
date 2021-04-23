package progiii.common.util;

public interface ClosableRes extends AutoCloseable {
    @Override
    void close() throws Exception;
}
