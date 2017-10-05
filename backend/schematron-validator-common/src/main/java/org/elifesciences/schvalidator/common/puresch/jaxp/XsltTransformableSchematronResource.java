package org.elifesciences.schvalidator.common.puresch.jaxp;

import com.helger.commons.io.resource.IReadableResource;
import com.helger.schematron.xslt.SCHTransformerCustomizer;
import com.helger.schematron.xslt.SchematronResourceSCH;

import javax.xml.transform.Transformer;
import java.util.List;

public class XsltTransformableSchematronResource extends SchematronResourceSCH {

	private final List<Transformer> transformerList;

	public XsltTransformableSchematronResource(IReadableResource aSCHResource, List<Transformer> transformerList) {
		super(aSCHResource);
		this.transformerList = transformerList;
	}

	@Override
	protected SCHTransformerCustomizer createTransformerCustomizer() {
		SCHTransformerCustomizer customizer = super.createTransformerCustomizer();
		transformerList.forEach(transformer -> customizer.customize(SCHTransformerCustomizer.EStep.SCH2XSLT_3, transformer));

		return customizer;
	}
}
