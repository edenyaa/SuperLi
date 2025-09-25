package Backend.ServiceLayer.ServiceLayerHR;

public class Response {

    public Object ReturnValue;
    public String ErrorMessage;

    public Response(String errorMessage) {
        this.ReturnValue = null;
        this.ErrorMessage = errorMessage;
    }
    public Response(Object returnValue) {
        this.ReturnValue = returnValue;
        this.ErrorMessage = null;
    }
    public Response(Object returnValue, String errorMessage) {
        this.ReturnValue = returnValue;
        this.ErrorMessage = errorMessage;
    }
    public Response() {
        this.ReturnValue = null;
        this.ErrorMessage = null;
    }

    public Object getReturnValue() {
        return ReturnValue;
    }

    public String getErrorMsg() {
        return ErrorMessage;
    }

    public boolean isSuccess() {
        return ReturnValue != null;
    }

}
