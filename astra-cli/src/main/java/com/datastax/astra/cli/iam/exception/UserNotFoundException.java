package com.datastax.astra.cli.iam.exception;

import com.datastax.astra.cli.core.out.LoggerShell;

/**
 * User not found
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class UserNotFoundException extends Exception {

    /** Serial Number. */
    private static final long serialVersionUID = -1134966974107948087L;

    /**
     * Default constructor
     */
    public UserNotFoundException() {}
    
    /**
     * Constructor with userName
     * 
     * @param userName
     *      user name
     */
    public UserNotFoundException(String userName) {
        super("User" + userName + "' has not been found.");
        LoggerShell.warning(getMessage());
    }

}
