// Copyright © LFV
package io.github.reqstool.processor;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.github.reqstool.annotations.Requirements;

public abstract class AbstractAnnotationsProcessor extends AbstractProcessor {

	public enum AnnotationTypes {

		REQUIREMENTS("implementations"), SVCS("tests");

		private String ymlElementKey;

		AnnotationTypes(String ymlElementKey) {
			this.ymlElementKey = ymlElementKey;
		}

		public String getYmlElementKey() {
			return ymlElementKey;
		}

	}

	private static final String YAML_LANG_SERVER_SCHEMA_INFO = "# yaml-language-server: $schema=https://raw.githubusercontent.com/reqstool/reqstool-client/main/src/reqstool/resources/schemas/v1/annotations.schema.json";

	private AnnotationTypes annotationTypes;

	@JsonPropertyOrder(alphabetic = true)
	private Map<String, List<AnnotationInfo>> annotationLocations;

	protected AbstractAnnotationsProcessor(AnnotationTypes requirementsAnnotationType) {
		this.annotationTypes = requirementsAnnotationType;
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		annotationLocations = new HashMap<>();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	@Requirements({ "ANNOTATIONS_002" })
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return exportToYAML();
		}

		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing annotations: " + annotations);

		for (TypeElement annotation : annotations) {

			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

			// for each annotated elemenet
			for (Element element : annotatedElements) {

				var annotationInfo = new AnnotationInfo(element.getEnclosingElement() + "." + element.getSimpleName(),
						element.getKind());

				// for each requirement id that this element has
				for (String requirementId : this.getAnnotationStrings(element)) {

					// get existing annotations for this requirement id
					List<AnnotationInfo> values = annotationLocations.get(requirementId);

					// create list, if no existing annotations for this requirement id
					values = (values == null ? new ArrayList<>() : values);
					values.add(annotationInfo);

					// add details about element for this requirement id to result
					annotationLocations.put(requirementId, values);
				}
			}
		}

		return true;
	}

	abstract List<String> getAnnotationStrings(Element element);

	@Requirements({ "ANNOTATIONS_003" })
	private boolean exportToYAML() {

		Map<String, Object> result = new LinkedHashMap<>();

		var om = new ObjectMapper(new YAMLFactory().enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));

		om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

		try {
			FileObject file = processingEnv.getFiler()
				.createResource(StandardLocation.SOURCE_OUTPUT, "resources", "annotations.yml");

			try (Writer writer = new PrintWriter(
					new OutputStreamWriter(file.openOutputStream(), StandardCharsets.UTF_8))) {

				Map<String, Object> requirementAnnotationMap = new LinkedHashMap<>();
				requirementAnnotationMap.put(annotationTypes.getYmlElementKey(), annotationLocations);

				result.put("requirement_annotations", requirementAnnotationMap);

				processingEnv.getMessager()
					.printMessage(Diagnostic.Kind.NOTE, "Writing Requirements Annotations data to: " + file.getName());

				// write JSON schema
				writer.write(YAML_LANG_SERVER_SCHEMA_INFO + System.lineSeparator());

				// write serialized result
				om.writeValue(writer, result);
			}
		}
		catch (IOException ex) {
			processingEnv.getMessager()
				.printMessage(Diagnostic.Kind.ERROR, "Exporting @Requirements mapping to JSON failed:" + ex);

			return false;
		}

		return true;
	}

}
