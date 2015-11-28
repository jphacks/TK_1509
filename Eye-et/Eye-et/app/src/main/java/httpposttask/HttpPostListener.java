package httpposttask;

/**
 * Created by toshiyukiando on 2015/10/10.
 */

public interface HttpPostListener {
    void postCompletion(byte[] response);
    void postFailure();
}