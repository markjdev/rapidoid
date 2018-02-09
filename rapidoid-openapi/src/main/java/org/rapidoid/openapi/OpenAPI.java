/*-
 * #%L
 * rapidoid-openapi
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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


package org.rapidoid.openapi;

import org.rapidoid.RapidoidThing;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.log.Log;
import org.rapidoid.render.Template;
import org.rapidoid.render.Templates;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.gui.GUI;

public class OpenAPI extends RapidoidThing {

    private static final Template OPENAPI_TEMPLATE = Templates.load("openapi-template.yaml");

    private static final Config OPENAPI = Conf.OPENAPI;

    public static void register(Setup setup) {

        if (OPENAPI.isEmpty()) {
            Log.warn("OpenAPI is not configured!");
        }

        final StringBuilder openApiYaml = new StringBuilder();
        openApiYaml.append(OPENAPI_TEMPLATE.render(U.map("openapi_project_version", "1.0", "openapi_project_title", "Project Title", "openapi_project_url", "http://api.vtracker.com.br")));

        final String openApiYamlPage = openApiYaml.toString();

        setup.get(Msc.specialUri("openapi")).plain(new ReqHandler() {
            @Override
            public Object execute(Req req) throws Exception {
                return GUI.hardcoded(openApiYamlPage);
            }
        });
    }

}
