package com.datastax.astra.cli.db;

import com.datastax.astra.cli.ExitCode;
import com.datastax.astra.cli.core.AbstractCmd;
import com.datastax.astra.cli.core.BaseCmd;
import com.datastax.astra.cli.core.exception.ParamValidationException;
import com.datastax.astra.cli.db.exception.DatabaseNameNotUniqueException;
import com.datastax.astra.cli.db.exception.DatabaseNotFoundException;
import com.datastax.astra.sdk.databases.domain.DatabaseStatusType;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;

/**
 * Create a DB with the CLI (initializing connection)
 *
 * astra db create NAME -r eu-east1 -ks ks1
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = AbstractCmd.CREATE, description = "Create a database with cli")
public class DbCreateCmd extends BaseCmd {
    
    /**
     * Database name or identifier
     */
    @Required
    @Arguments(title = "DB_NAME", description = "Database name (not unique)")
    protected String databaseName;
    
    /** 
     * Database or keyspace are created when needed
     **/
    @Option(name = { "--if-not-exist", "--if-not-exists" }, 
            description = "will create a new DB only if none with same name")
    protected boolean ifNotExist = false;

    /**
     * Cloud provider region to provision
     */
    @Option(name = { "-r", "--region" }, title = "DB_REGION", arity = 1, 
            description = "Cloud provider region to provision")
    protected String databaseRegion = OperationsDb.DEFAULT_REGION;
    
    /**
     * Default keyspace created with the Db
     */
    @Option(name = { "-k", "--keyspace" }, title = "KEYSPACE", arity = 1, 
            description = "Default keyspace created with the Db")
    protected String defaultKeyspace;
    
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
            description = "Provide a limit to the wait period in seconds, default is 180s.")
    protected int timeout = 180;
    
    /** {@inheritDoc} */
    @Override
    public ExitCode execute() 
    throws DatabaseNameNotUniqueException, DatabaseNotFoundException, ParamValidationException {
        ExitCode code = OperationsDb.createDb(databaseName, databaseRegion, defaultKeyspace, ifNotExist);
        if (ExitCode.SUCCESS.equals(code) && wait) {
            return OperationsDb.waitForDbStatus(databaseName, DatabaseStatusType.ACTIVE, timeout);
        }
        return code;
    }
    
}