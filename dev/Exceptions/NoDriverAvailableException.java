package Exceptions;

public class NoDriverAvailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String license;

    public NoDriverAvailableException(String license) {
        super("No driver available with license: " + license + " or no driver available at all");
        this.license = license;
        
    }

    public String getLicense() {
        return license;
    }
    
}
