// Copyright © LFV
package io.github.reqstool.annotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;

class SVCsTests {

	@Test
	@SVCs({ "SVC_ANNOTATIONS_001" })
	void testOneSVC() {

		SVCs svcs = new SVCs() {

			@Override
			public String[] value() {
				return new String[] { "VALUE" };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return SVCs.class;
			}
		};

		assertThat(svcs.value()[0], is("VALUE"));
	}

	@Test
	@SVCs({ "SVC_ANNOTATIONS_001" })
	void testMultipleSVCs() {

		SVCs svcs = new SVCs() {

			@Override
			public String[] value() {
				return new String[] { "VALUE1", "VALUE2" };
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return SVCs.class;
			}
		};

		assertThat(svcs.value(), is(new String[] { "VALUE1", "VALUE2" }));
	}

}
