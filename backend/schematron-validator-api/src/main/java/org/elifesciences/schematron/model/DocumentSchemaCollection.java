package org.elifesciences.schematron.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * A collection of {@link DocumentSchema}s registered with the application.
 */
public final class DocumentSchemaCollection implements Iterable<DocumentSchema> {

	/**
	 * A mapping of schema names to {@link DocumentSchema} objects.
	 */
	private final Map<String, DocumentSchema> schemaMap;

	public DocumentSchemaCollection(Collection<DocumentSchema> schemas) {
		this.schemaMap = schemas.stream().collect(toMap(DocumentSchema::getId, Function.identity()));
	}

	/**
	 * Finds the {@link DocumentSchema} with the given name.
	 *
	 * @param id The name of the {@code DocumentSchema} to find.
	 * @return The matching schema.
	 * @throws NoSuchSchemaException if there is no matching {@code DocumentSchema}.
	 */
	public DocumentSchema find(String id) throws NoSuchSchemaException {
		return Optional.ofNullable(schemaMap.get(id))
			.orElseThrow(NoSuchSchemaException::new);
	}

	@Override
	public Iterator<DocumentSchema> iterator() {
		return schemaMap.values().iterator();
	}
}
