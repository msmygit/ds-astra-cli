package com.dtsx.astra.cli.org;

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

import com.dtsx.astra.cli.core.AbstractConnectedCmd;
import com.github.rvesse.airline.annotations.Command;

/**
 * Show organization name
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Command(name = OrganizationService.CMD_NAME, description = "Show organization name.")
public class OrgNameCmd extends AbstractConnectedCmd {

    /** {@inheritDoc} */
    public void execute() {
        OrganizationService.getInstance().getName();
    }

}