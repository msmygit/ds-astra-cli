package com.dtsx.astra.cli;

/*-
 * #%L
 * Astra Cli
 * %%
 * Copyright (C) 2022 DataStax
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.dtsx.astra.cli.config.*;
import com.dtsx.astra.cli.core.AbstractCmd;
import com.dtsx.astra.cli.core.CliContext;
import com.dtsx.astra.cli.core.DefaultCmd;
import com.dtsx.astra.cli.core.ExitCode;
import com.dtsx.astra.cli.core.exception.*;
import com.dtsx.astra.cli.core.out.AstraCliConsole;
import com.dtsx.astra.cli.core.out.LoggerShell;
import com.dtsx.astra.cli.db.*;
import com.dtsx.astra.cli.db.cqlsh.DbCqlShellCmd;
import com.dtsx.astra.cli.db.dsbulk.DbCountCmd;
import com.dtsx.astra.cli.db.dsbulk.DbLoadCmd;
import com.dtsx.astra.cli.db.dsbulk.DbUnLoadCmd;
import com.dtsx.astra.cli.db.exception.*;
import com.dtsx.astra.cli.db.keyspace.DbCreateKeyspaceCmd;
import com.dtsx.astra.cli.db.keyspace.DbListKeyspacesCmd;
import com.dtsx.astra.cli.db.region.*;
import com.dtsx.astra.cli.db.tool.DbGraphqlPlaygroundCmd;
import com.dtsx.astra.cli.db.tool.DbSwaggerUICmd;
import com.dtsx.astra.cli.iam.role.RoleGetCmd;
import com.dtsx.astra.cli.iam.role.RoleListCmd;
import com.dtsx.astra.cli.iam.role.exception.RoleNotFoundException;
import com.dtsx.astra.cli.iam.token.TokenCreateCmd;
import com.dtsx.astra.cli.iam.token.TokenDeleteCmd;
import com.dtsx.astra.cli.iam.token.TokenListCmd;
import com.dtsx.astra.cli.iam.token.TokenRevokeCmd;
import com.dtsx.astra.cli.iam.user.UserDeleteCmd;
import com.dtsx.astra.cli.iam.user.UserGetCmd;
import com.dtsx.astra.cli.iam.user.UserInviteCmd;
import com.dtsx.astra.cli.iam.user.UserListCmd;
import com.dtsx.astra.cli.iam.user.exception.UserAlreadyExistException;
import com.dtsx.astra.cli.iam.user.exception.UserNotFoundException;
import com.dtsx.astra.cli.org.OrgCmd;
import com.dtsx.astra.cli.org.OrgIdCmd;
import com.dtsx.astra.cli.org.OrgNameCmd;
import com.dtsx.astra.cli.streaming.*;
import com.dtsx.astra.cli.streaming.exception.TenantAlreadyExistException;
import com.dtsx.astra.cli.streaming.exception.TenantNotFoundException;
import com.dtsx.astra.cli.streaming.pulsarshell.PulsarShellCmd;
import com.dtsx.astra.cli.utils.AstraCliUtils;
import com.dtsx.astra.sdk.db.domain.exception.RegionNotFoundException;
import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.annotations.Group;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.parser.errors.*;
import org.fusesource.jansi.AnsiConsole;

import java.util.Arrays;

/**
 * Main class for the program. Will route commands to proper class 
 */
@com.github.rvesse.airline.annotations.Cli(
  name = "astra", 
  description = "CLI for DataStax Astra™ ",
  defaultCommand = DefaultCmd.class, 
  commands = { 
    SetupCmd.class, Help.class, DefaultCmd.class
  },
  groups = {
    
    @Group(
       name = "config", 
       description = "Manage configuration file", 
       defaultCommand = ConfigListCmd.class,
       commands = {
         ConfigCreateCmd.class, ConfigGetCmd.class, ConfigDeleteCmd.class,
         ConfigListCmd.class, ConfigUseCmd.class
    }),
   
    @Group(name = "org", 
      description = "Display Organization Info", 
      defaultCommand = OrgCmd.class,
      commands = {
        OrgIdCmd.class,
        OrgNameCmd.class
    }),
    
    @Group(
       name = "db", 
       description = "Manage databases",
       defaultCommand = DbListCmd.class,
       commands = { 
         // Create,delete
         DbCreateCmd.class,  DbDeleteCmd.class,
         // Read
         DbListCmd.class,  DbGetCmd.class, DbStatusCmd.class,
         // Operation
         DbResumeCmd.class, DbDownloadScbCmd.class, DbCreateDotEnvCmd.class,
         // DsBulk
         DbCountCmd.class, DbLoadCmd.class, DbUnLoadCmd.class,
         // Cqlshell
         DbCqlShellCmd.class,
         // Work with Keyspaces
         DbCreateKeyspaceCmd.class, DbListKeyspacesCmd.class,
         // Work with Regions
         DbCreateRegionCmd.class, DbListRegionsCmd.class, DbDeleteRegionCmd.class,
         // List Region
         DbListRegionsClassicCmd.class, DbListRegionsServerlessCmd.class,
         // External Tools
         DbSwaggerUICmd.class, DbGraphqlPlaygroundCmd.class
     }),
    
    @Group(
       name = "streaming", 
       description = "Manage Streaming tenants", 
       defaultCommand = StreamingListCmd.class,
       commands = { 
         // Create, Delete
         StreamingCreateCmd.class, StreamingDeleteCmd.class,
         // Read
         StreamingListCmd.class, StreamingGetCmd.class,
         StreamingExistCmd.class, StreamingStatusCmd.class,
         StreamingPulsarTokenCmd.class, StreamingCreateDotEnvCmd.class,
         // list Regions
         StreamingListRegionsCmd.class,
         // Pulsar Shell
         PulsarShellCmd.class,
         // Change Data Capture
         // StreamingCreateCdcCmd.class, StreamingDeleteCdcCmd.class, StreamingGetCdcCmd.class
    }),
    
    @Group(
       name= "role", 
       description = "Manage roles", 
       defaultCommand = RoleListCmd.class,
       commands = {
         RoleListCmd.class, RoleGetCmd.class
    }),
    
    @Group(
       name= "user", 
       description = "Manage users", 
       defaultCommand = UserListCmd.class,
       commands = {
         UserGetCmd.class, UserInviteCmd.class, UserDeleteCmd.class,
         UserListCmd.class
    }),

    @Group(
       name= "token",
       description = "Manage tokens",
       defaultCommand = TokenListCmd.class,
       commands = {
         TokenListCmd.class, TokenCreateCmd.class, TokenDeleteCmd.class, TokenRevokeCmd.class
     })
})
public class AstraCli {
    
    /**
     * Main Program.
     *
     * @param args
     *           start options for the shell
     */
    public static void main(String[] args) {
        // Enable Colors in terminal
        AnsiConsole.systemInstall();

        // Create ~/.astra and required sub folders
        AstraCliUtils.createHomeAstraFolders();

        // Parse command
        ExitCode code = run(AstraCli.class, args);
        
        // Enable Colors in terminal
        AnsiConsole.systemUninstall();
        
        // Exit with proper to code
        System.exit(code.getCode());
    }
    
    /**
     * Parsing command and error relative to command.
     * 
     * @param clazz
     *      class name to marshall
     * @param args
     *      command line arguments
     * @return
     *      code for the parsing
     */
    public static ExitCode run(Class<?> clazz, String[] args) {
        try {
            
            // Persist command line to log it later
            CliContext.getInstance().setArguments(Arrays.asList(args));
            
            // Parse and Run
            new Cli<AbstractCmd>(clazz).parse(args).run();
            
            // Return all good
            return ExitCode.SUCCESS;
            
        } catch(ClassCastException ce) {
            // Help does its own things
            new Cli<Runnable>(clazz).parse(args).run();
            return ExitCode.SUCCESS;
        } catch(ParseArgumentsUnexpectedException |
                ParseArgumentsMissingException    |
                ParseTooManyArgumentsException    |
                ParseOptionGroupException ex) {
            LoggerShell.exception(ex,
                    "You provided unknown or not well formatted argument.");
            return ExitCode.INVALID_ARGUMENT;
        } catch(ParseOptionIllegalValueException | ParseOptionMissingException ex) {
            LoggerShell.exception(ex,
                    "You provided unknown or not well formatted option. (-option)");
            return ExitCode.INVALID_OPTION;
        } catch(ParseRestrictionViolatedException | ParseOptionConversionException ex) {
            LoggerShell.exception(ex,
                    "You provided an invalid value for option. (-option)");
            return ExitCode.INVALID_OPTION_VALUE;
        } catch(ParseException ex) {
            LoggerShell.exception(ex,
                    "Command is not properly formatted.");
            return ExitCode.UNRECOGNIZED_COMMAND;
        } catch (InvalidTokenException | TokenNotFoundException |
                FileSystemException | ConfigurationException e) {
            AstraCliConsole.outputError(ExitCode.CONFIGURATION, e.getMessage());
            return ExitCode.CONFIGURATION;
        } catch (InvalidArgumentException dex) {
           AstraCliConsole.outputError(ExitCode.INVALID_ARGUMENT, dex.getMessage());
           return  ExitCode.INVALID_ARGUMENT;
        } catch (DatabaseNotFoundException |
                TenantNotFoundException |
                RoleNotFoundException |
                UserNotFoundException | RegionNotFoundException ex) {
           AstraCliConsole.outputError(ExitCode.NOT_FOUND, ex.getMessage());
           return ExitCode.NOT_FOUND;
       } catch (DatabaseNameNotUniqueException | KeyspaceAlreadyExistException |
                TenantAlreadyExistException | UserAlreadyExistException | RegionAlreadyExistException e) {
           AstraCliConsole.outputError(ExitCode.ALREADY_EXIST, e.getMessage());
           return ExitCode.ALREADY_EXIST;
       } catch(InvalidDatabaseStateException ex) {
            AstraCliConsole.outputError(ExitCode.UNAVAILABLE, ex.getMessage());
            return ExitCode.UNAVAILABLE;
        } catch (Exception ex) {
            AstraCliConsole.outputError(ExitCode.INTERNAL_ERROR, ex.getMessage());
            return ExitCode.INTERNAL_ERROR;
       }
    }
    
}
