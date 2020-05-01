package Helpers;

import java.io.IOException;

public interface ICallback {

    void invoke(Object... args) throws Exception;
}
