package com.datastax.astra.cli.iam;

import com.datastax.astra.cli.core.AbstractCmd;
import com.datastax.astra.cli.core.BaseSh;
import com.datastax.astra.cli.iam.exception.UserNotFoundException;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Delete a user if exist
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = AbstractCmd.DELETE, description = "Delete an existing user")
public class UserDeleteSh extends BaseSh {
    
    /**
     * Database name or identifier
     */
    @Required
    @Arguments(title = "EMAIL", description = "User email or identifier")
    public String user;
    
    /** {@inheritDoc} */
    public void execute() throws UserNotFoundException {
        OperationIam.deleteUser(this, user);
    }
    
}
