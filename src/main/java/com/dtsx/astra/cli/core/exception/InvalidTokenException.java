package com.dtsx.astra.cli.core.exception;

/*-
 * #%L
 * Astra CLI
 * --
 * Copyright (C) 2022 - 2023 DataStax
 * --
 * Licensed under the Apache License, Version 2.0
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
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
 * Invalid parameter provided.
 */
public class InvalidTokenException extends RuntimeException {

    /** 
     * Serial
     */
    @Serial
    private static final long serialVersionUID = -7071867151543923236L;


    
    /**
     * Constructor with token
     * 
     * @param token
     *      invalid token
     */
    public InvalidTokenException(String token) {
        super("Token '" + token + "' is invalid");
    }

    /**
     * Constructor with token
     *
     * @param token
     *      invalid token
     * @param parent
     *      parent exception
     */
    public InvalidTokenException(String token, Throwable parent) {
        super("Token '" + token + "' is invalid: " + parent.getMessage(), parent);
    }

}
