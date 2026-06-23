// Copyright © LFV
package io.github.reqstool.processor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.annotation.processing.AbstractProcessor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import io.github.reqstool.annotations.SVCs;

/**
 * Test custom AnnotationsProcessorTests
 *
 * Currently, it just checks that the processor does not fail during compile. The current
 * test does not check the content of the generated file etc. Nir is it able to test the
 * actual maven build cycle.
 *
 */
class AnnotationsProcessorTests {

	@TempDir
	Path tempDir;

	@Test
	@SVCs({ "SVC_ANNOTATIONS_002", "SVC_ANNOTATIONS_003" })
	void testRequirementsAnnotations() throws IOException {

		String javaFileResourceName = "java/RequirementsExample.java";
		String ymlFileResourceName = "yml/requirements_annotations.yml";
		String generatedFilePath = "/resources/annotations.yml";

		testAnnotationsProcessor(new RequirementsProcessor(), javaFileResourceName, ymlFileResourceName,
				generatedFilePath);
	}

	@Test
	@SVCs({ "SVC_ANNOTATIONS_002", "SVC_ANNOTATIONS_003" })
	void testSVCsAnnotations() throws IOException {

		String javaFileResourceName = "java/SVCsExample.java";
		String ymlFileResourceName = "yml/svcs_annotations.yml";
		String generatedFilePath = "/resources/annotations.yml";

		testAnnotationsProcessor(new SVCsProcessor(), javaFileResourceName, ymlFileResourceName, generatedFilePath);
	}

	private void testAnnotationsProcessor(AbstractAnnotationsProcessor aap, String javaFileResourceName,
			String ymlFileResourceName, String generatedFilePath) throws IOException {
		// Given
		var compiler = ToolProvider.getSystemJavaCompiler();

		var diagnostics = new DiagnosticCollector<JavaFileObject>();

		var fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempDir.toFile()));
		fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(tempDir.toFile()));

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(javaFileResourceName).getFile());

		var compilationUnits = fileManager.getJavaFileObjects(file);

		var task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

		task.setProcessors(Arrays.asList(new AbstractProcessor[] { aap }));

		// When
		boolean success = task.call();

		// Then
		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
			System.out.println(diagnostic);
		}

		assertThat(success, is(true));

		File generatedFile = new File(tempDir.toFile() + generatedFilePath);
		File expectedFile = new File(classLoader.getResource(ymlFileResourceName).getFile());

		String generatedFileString = Files.readString(generatedFile.toPath());
		String expectedFileString = Files.readString(expectedFile.toPath());

		assertThat(generatedFileString, is(expectedFileString));
	}

}
