/*-
 * #%L
 * rapidoid-http-fast
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

package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.6.0")
public class RouteMeta extends RapidoidThing {

	private volatile String id;

	private volatile String summary;

	private volatile String description;

	private volatile Set<String> tags = U.set();

	private volatile Map<String, Object> schema = U.map();

	@Override
	public String toString() {
		return "RouteMeta{" +
			"id='" + id + '\'' +
			", summary='" + summary + '\'' +
			", description='" + description + '\'' +
			", tags=" + tags +
			", schema=" + schema +
			'}';
	}

	public String id() {
		return id;
	}

	public RouteMeta id(String id) {
		this.id = id;
		return this;
	}

	public String summary() {
		return summary;
	}

	public RouteMeta summary(String summary) {
		this.summary = summary;
		return this;
	}

	public Set<String> tags() {
		return tags;
	}

	public RouteMeta tags(Set<String> tags) {
		this.tags = tags;
		return this;
	}

	public Map<String, Object> schema() {
		return schema;
	}

	public RouteMeta schema(Map<String, Object> schema) {
		this.schema = schema;
		return this;
	}

	public RouteMeta copy() {
		RouteMeta copy = new RouteMeta();

		copy.id = this.id;
		copy.summary = this.summary;
		copy.description = this.description;
		copy.tags = Coll.copyOf(this.tags);
		copy.schema = Coll.deepCopyOf(this.schema);

		return copy;
	}
}
