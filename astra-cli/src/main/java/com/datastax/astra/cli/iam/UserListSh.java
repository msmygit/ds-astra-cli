package com.datastax.astra.cli.iam;

import com.datastax.astra.cli.core.AbstractCmd;
import com.datastax.astra.cli.core.BaseSh;
import com.github.rvesse.airline.annotations.Command;

/**
 * Display roles.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = AbstractCmd.LIST, description = "Display the list of Users in an organization")
public class UserListSh extends BaseSh {
   
    /** {@inheritDoc} */
    public void execute() {
        OperationIam.listUsers(this);
    }
    
}
