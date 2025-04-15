package exception;

import enums.ErrorType;

public class UMASException extends Exception {

    public UMASException(ErrorType errorType, String message){
        super("[[[" + errorType.toString() + "]]]\n" + message);
    }

}
