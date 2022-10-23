package com.datastax.astra.cli.db;

import com.datastax.astra.cli.core.exception.InvalidArgumentException;
import com.datastax.astra.cli.db.exception.DatabaseNameNotUniqueException;
import com.datastax.astra.cli.db.exception.DatabaseNotFoundException;
import com.datastax.astra.cli.db.exception.InvalidDatabaseStateException;
import com.datastax.astra.sdk.databases.domain.DatabaseStatusType;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;

/**
 * Delete a DB is exist
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = "delete", description = "Delete an existing database")
public class DbDeleteCmd extends AbstractDatabaseCmd {

    /**
     * Will wait until the database become ACTIVE.
     */
    @Option(name = { "--wait" },
            description = "Will wait until the database become ACTIVE")
    protected boolean wait = false;

    /**
     * Provide a limit to the wait period in seconds, default is 180s.
     */
    @Option(name = { "--timeout" },
            description = "Provide a limit to the wait period in seconds, default is 300s.")
    protected int timeout = 300;

    /** {@inheritDoc} */
    public void execute() {
        dbServices.deleteDb(db);
        if (wait &&  dbServices.retryUntilDbDeleted(db, timeout) >= timeout) {
               throw new InvalidDatabaseStateException(db, DatabaseStatusType.TERMINATED,
                       DatabaseStatusType.TERMINATING);
        }
    }
    
}
