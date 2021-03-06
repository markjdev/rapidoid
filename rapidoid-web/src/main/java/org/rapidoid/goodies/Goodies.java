/*-
 * #%L
 * rapidoid-web
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

package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.dbadmin.ManageableRdbms;
import org.rapidoid.goodies.discovery.DiscoveryIndexHandler;
import org.rapidoid.goodies.discovery.DiscoveryRegistrationHandler;
import org.rapidoid.goodies.discovery.DiscoveryState;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.insight.Metrics;
import org.rapidoid.jpa.JPA;
import org.rapidoid.security.Role;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

import java.lang.reflect.Method;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Goodies extends RapidoidThing {

	public static final JMXGoodies JMX = new JMXGoodies();

	public static final String CENTER = "center";

	public static void bootstrap(Setup setup) {
		if (setup.isAdmin()) {
			adminCenter(setup);

		} else if (setup.isApp()) {
			bootstrapAppGoodies(setup);
		}
	}

	public static void bootstrapAppGoodies(Setup setup) {
		Msc.logSection("Registering App services:");

		auth(setup);
	}

	public static void adminCenter(Setup setup) {
		Msc.logSection("Registering Admin Center:");

		Metrics.bootstrap();

		overview(setup);
		entities(setup);
		application(setup);
		lifecycle(setup);
		manageables(setup);
		jmx(setup);
		metrics(setup);
		auth(setup);
		status(setup);
	}

	public static void auth(Setup setup) {
		setup.post(uri("login")).roles().json(new LoginHandler());
		setup.get(uri("logout")).roles(Role.LOGGED_IN).json(new LogoutHandler());
	}

	public static void ping(Setup setup) {
		setup.get(uri("ping")).plain("OK");
	}

	public static void lifecycle(Setup setup) {
		setup.page(uri("terminate")).zone(CENTER).mvc(new TerminateHandler());
	}

	public static void overview(Setup setup) {
		setup.page(uri("")).zone(CENTER).zone(CENTER).mvc(new OverviewHandler());
	}

	public static void application(Setup setup) {
		setup.page(uri("routes")).zone(CENTER).mvc(new RoutesHandler());
		setup.page(uri("beans")).zone(CENTER).mvc(new BeansHandler());
		setup.page(uri("config")).zone(CENTER).mvc(new ConfigHandler());
		setup.get(uri("classpath")).zone(CENTER).mvc(new ClasspathHandler());
	}

	public static void metrics(Setup setup) {
		setup.page(uri("metrics")).zone(CENTER).mvc(new GraphsHandler());
		setup.get(uri("graphs/{id:.*}")).json(new GraphDataHandler());
	}

	public static void manageables(Setup setup) {
		String baseUri = uri("manageables");

		ManageablesOverviewPage overview = new ManageablesOverviewPage()
			.baseUri(baseUri);

		setup.page(baseUri).zone(CENTER).mvc(overview);

		ManageableDetailsPage details = new ManageableDetailsPage()
			.baseUri(baseUri);

		setup.page(uri("manageables/{type}/{id}/*")).zone(CENTER).mvc(details);
	}

	public static void dbAdmin(Setup setup) {
		String baseUri = uri("db");

		ManageablesOverviewPage overview = new ManageablesOverviewPage()
			.groupType(ManageableRdbms.class)
			.baseUri(baseUri);

		setup.page(baseUri).zone(CENTER).mvc(overview);

		ManageableDetailsPage details = new ManageableDetailsPage()
			.baseUri(baseUri);

		setup.page(baseUri + "/{type}/{id}/*").zone(CENTER).mvc(details);
	}

	public static void jmx(Setup setup) {
		setup.page(uri("jmx/memory")).zone(CENTER).mvc(JMX.memory());
		setup.page(uri("jmx/mempool")).zone(CENTER).mvc(JMX.memoryPool());
		setup.page(uri("jmx/classes")).zone(CENTER).mvc(JMX.classes());
		setup.page(uri("jmx/os")).zone(CENTER).mvc(JMX.os());
		setup.page(uri("jmx/threads")).zone(CENTER).mvc(JMX.threads());
		setup.page(uri("jmx/compilation")).zone(CENTER).mvc(JMX.compilation());
		setup.page(uri("jmx/runtime")).zone(CENTER).mvc(JMX.runtime());
		setup.page(uri("jmx/gc")).zone(CENTER).mvc(JMX.gc());
	}

	public static void entities(Setup setup) {
		setup.page(uri("entities")).zone(CENTER).mvc(new EntitiesHandler());

		if (MscOpts.hasJPA()) {
			for (Class<?> type : JPA.getEntityJavaTypes()) {
				String uri = GUI.typeUri(type);
				String contextPath = HttpUtils.zone(setup.custom(), setup.zone()).entry("home").or(uri(""));
				X.scaffold(setup, Msc.uri(contextPath, uri), type);
			}
		}
	}

	public static void oauth(Setup setup) {
		Class<?> oauthClass = Cls.getClassIfExists("org.rapidoid.oauth.OAuth");
		U.must(oauthClass != null, "Cannot find the OAuth components, is module 'rapidoid-oauth' missing?");

		Method bootstrap = Cls.getMethod(oauthClass, "bootstrap", Setup.class);

		Cls.invokeStatic(bootstrap, setup);
	}

	public static void welcome(Setup setup) {
		if (!setup.routes().hasRouteOrResource(HttpVerb.GET, "/")) {
			On.get("/").view("_welcome").zone(CENTER).mvc(new WelcomeHandler());
		}
	}

	public static void status(Setup setup) {
		setup.get(uri("status")).json(new StatusHandler());
	}

	public static void discovery(Setup setup) {
		DiscoveryState state = new DiscoveryState();

		setup.post(uri("discovery/{scope}/register")).json(new DiscoveryRegistrationHandler(state));
		setup.get(uri("discovery/{scope}")).json(new DiscoveryIndexHandler(state));
	}

	public static void echo(Setup setup) {
		setup.get(uri("echo")).json(new EchoHandler());
	}

	static String uri(String path) {
		return Msc.specialUri(path);
	}

}
