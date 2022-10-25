package com.datastax.astra.cli.iam.exception;

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

import java.io.Serial;

/**
 * Role not found
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RoleNotFoundException extends RuntimeException {

    /** Serial Number. */
    @Serial
    private static final long serialVersionUID = -1269813351970244235L;
   
    /**
     * Constructor with roleName
     * 
     * @param roleName
     *      role name
     */
    public RoleNotFoundException(String roleName) {
        super("Role '" + roleName + "' has not been found.");
    }

}
