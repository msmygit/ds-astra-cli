package com.dtsx.astra.cli.iam;

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

import com.dtsx.astra.cli.core.CliContext;
import com.dtsx.astra.cli.core.ExitCode;
import com.dtsx.astra.cli.core.out.AstraCliConsole;
import com.dtsx.astra.cli.core.out.JsonOutput;
import com.dtsx.astra.cli.core.out.ShellTable;
import com.dtsx.astra.cli.iam.exception.RoleNotFoundException;
import com.dtsx.astra.cli.iam.exception.UserAlreadyExistException;
import com.dtsx.astra.cli.iam.exception.UserNotFoundException;
import com.dtsx.astra.sdk.organizations.OrganizationsClient;
import com.dtsx.astra.sdk.organizations.domain.Role;
import com.dtsx.astra.sdk.organizations.domain.User;
import com.dtsx.astra.sdk.utils.IdUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for command `role`
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class OperationIam {
    
    /** working with roles. */
    public static final String COMMAND_ROLE = "role";
    /** Column name. */
    private static final String COLUMN_ROLE_ID          = "Role Id";
    /** Column name. */
    private static final String COLUMN_ROLE_NAME        = "Role Name";
    /** Column name. */
    private static final String COLUMN_ROLE_DESCRIPTION = "Description";
    
    /** Column name. */
    private static final String COLUMN_USER_ID          = "User Id";
    /** Column name. */
    private static final String COLUMN_USER_EMAIL       = "User Email";
    /** Column name. */
    private static final String COLUMN_USER_STATUS      = "Status";

    /**
     * Hide default constructor.
     */
    private OperationIam() {}

    /**
     * List Roles.
     */
    public static void listRoles() {
        ShellTable sht = new ShellTable();
        sht.addColumn(COLUMN_ROLE_ID, 37);
        sht.addColumn(COLUMN_ROLE_NAME, 20);
        sht.addColumn(COLUMN_ROLE_DESCRIPTION, 20);
        CliContext.getInstance()
                    .getApiDevopsOrganizations()
                    .roles()
                    .forEach(role -> {
             Map <String, String> rf = new HashMap<>();
             rf.put(COLUMN_ROLE_ID, role.getId());
             rf.put(COLUMN_ROLE_NAME, role.getName());
             rf.put(COLUMN_ROLE_DESCRIPTION, role.getPolicy().getDescription());
             sht.getCellValues().add(rf);
        });
        AstraCliConsole.printShellTable(sht);
    }
    
    /**
     * List Roles.
     */
    public static void listUsers() {
        ShellTable sht = new ShellTable();
        sht.addColumn(COLUMN_USER_ID, 37);
        sht.addColumn(COLUMN_USER_EMAIL, 20);
        sht.addColumn(COLUMN_USER_STATUS, 20);
        CliContext.getInstance()
                    .getApiDevopsOrganizations()
                    .users().forEach(user -> {
             Map <String, String> rf = new HashMap<>();
             rf.put(COLUMN_USER_ID, user.getUserId());
             rf.put(COLUMN_USER_EMAIL, user.getEmail());
             rf.put(COLUMN_USER_STATUS, user.getStatus().name());
             sht.getCellValues().add(rf);
        });
        AstraCliConsole.printShellTable(sht);
    }
    
    /**
     * Show Role details.
     *
     * @param role
     *      role name
     * @throws RoleNotFoundException
     *      role has not been found
     */
    public static void showRole(String role) throws RoleNotFoundException {
        Optional<Role> optRole = CliContext
                .getInstance()
                .getApiDevopsOrganizations()
                .findRoleByName(role);
            
        if (optRole.isEmpty() && IdUtils.isUUID(role)) {
            optRole = CliContext
                 .getInstance()
                 .getApiDevopsOrganizations()
                 .role(role)
                 .find();
            }
            
        if (optRole.isEmpty()) {
            throw new RoleNotFoundException(role);
        }
            
        Role r = optRole.get();
        ShellTable sht = ShellTable.propertyTable(15, 40);
        sht.addPropertyRow("Identifier",    r.getId());
        sht.addPropertyRow("Name",          r.getName());
        sht.addPropertyRow(COLUMN_ROLE_DESCRIPTION,   r.getPolicy().getDescription());
        sht.addPropertyRow("Effect",        r.getPolicy().getEffect());
        switch (CliContext.getInstance().getOutputFormat()) {
            case CSV -> {
                sht.addPropertyRow("Resources", r.getPolicy().getResources().toString());
                sht.addPropertyRow("Actions", r.getPolicy().getActions().toString());
                AstraCliConsole.printShellTable(sht);
            }
            case JSON -> AstraCliConsole.printJson(new JsonOutput<>(ExitCode.SUCCESS,
                    OperationIam.COMMAND_ROLE + " get " + role, r));
            case HUMAN -> {
                sht.addPropertyListRows("Resources", r.getPolicy().getResources());
                sht.addPropertyListRows("Actions", r.getPolicy().getActions());
                AstraCliConsole.printShellTable(sht);
            }
        }
    }
    
    /**
     * Show User details.
     *
     * @param user
     *      user email
     * @throws UserNotFoundException
     *      user has not been found
     */
    public static void showUser(String user) throws UserNotFoundException {
       Optional<User> optUser = CliContext
               .getInstance()
               .getApiDevopsOrganizations()
               .findUserByEmail(user);
            
       if (optUser.isEmpty() && IdUtils.isUUID(user)) {
           optUser = CliContext
                .getInstance()
                .getApiDevopsOrganizations()
                .user(user)
                .find();
       }
            
       if (optUser.isEmpty()) {
           throw new UserNotFoundException(user);
       }
       
       User r = optUser.get();
       ShellTable sht = ShellTable.propertyTable(15, 40);
       sht.addPropertyRow(COLUMN_USER_ID,   r.getUserId());
       sht.addPropertyRow(COLUMN_USER_EMAIL,  r.getEmail());
       sht.addPropertyRow(COLUMN_USER_STATUS, r.getStatus().name());
       
       List<String> roleNames =  r.getRoles()
               .stream()
               .map(Role::getName)
               .toList();

        switch (CliContext.getInstance().getOutputFormat()) {
            case CSV -> {
                sht.addPropertyRow("Roles", roleNames.toString());
                AstraCliConsole.printShellTable(sht);
            }
            case JSON -> AstraCliConsole.printJson(new JsonOutput<>(ExitCode.SUCCESS, "user show " + user, r));
            case HUMAN -> {
                sht.addPropertyListRows("Roles", roleNames);
                AstraCliConsole.printShellTable(sht);
            }
        }
    }
    
    /**
     * Invite User.
     *
     * @param user
     *      user email
     * @param role
     *      target role for the user
     * @throws UserAlreadyExistException
     *      user does not exist
     * @throws RoleNotFoundException
     *      role does not exist 
     */
    public static void inviteUser(String user, String role) throws UserAlreadyExistException, RoleNotFoundException {
        OrganizationsClient oc = CliContext.getInstance().getApiDevopsOrganizations();
        Optional<User> optUser = oc.findUserByEmail(user);
        if (optUser.isPresent()) {
            throw new UserAlreadyExistException(user);
        }
        Optional<Role> optRole = oc.findRoleByName(role);
        if (optRole.isEmpty() && IdUtils.isUUID(role)) {
            optRole = oc.role(role).find();
        }
        if (optRole.isEmpty()) {
            throw new RoleNotFoundException(role);
        }
        oc.inviteUser(user, optRole.get().getId());
        AstraCliConsole.outputSuccess(role);
    }

    /**
     * Delete a user if exist.
     * @param user
     *      user email of technical identifier
     * @throws UserNotFoundException
     *      user not found
     */
    public static void deleteUser(String user)
    throws UserNotFoundException {
        OrganizationsClient oc = CliContext.getInstance().getApiDevopsOrganizations();
        Optional<User> optUser = oc.findUserByEmail(user);
        if (optUser.isEmpty() && IdUtils.isUUID(user)) {
            optUser = oc.user(user).find();
        }
        if (optUser.isEmpty()) {
            throw new UserNotFoundException(user);
        }
        oc.user(optUser.get().getUserId()).delete();
        AstraCliConsole.outputSuccess("Deleting user '" + user + "' (async operation)");
    }
    
}