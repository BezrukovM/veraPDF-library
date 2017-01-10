/**
 * This file is part of veraPDF Library core, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * veraPDF Library core is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with veraPDF Library core as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * veraPDF Library core as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.verapdf.policy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.verapdf.core.VeraPDFException;

/**
 * @author <a href="mailto:carl@openpreservation.org">Carl Wilson</a>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>
 * @version 0.1 Created 12 Dec 2016:17:51:12
 */

public final class PolicyChecker {
	private static final TransformerFactory factory = TransformerFactory.newInstance();
	private static final String extensionRegEx = "\\.(?=[^\\.]+$)"; //$NON-NLS-1$
	private static final String schematronExt = "sch"; //$NON-NLS-1$
	private static final String xslExtension = "xsl"; //$NON-NLS-1$
	private static final String xsltExtension = "xslt"; //$NON-NLS-1$
	private static final String xslExt = "." + xslExtension; //$NON-NLS-1$
	public static final List<String> allowedExtensions = Arrays.asList(schematronExt, xslExtension, xsltExtension);
	private static final String resourcePath = "org/verapdf/policy/"; //$NON-NLS-1$
	private static final String mergeXsl = resourcePath + "MergeMrrPolicy" + xslExt; //$NON-NLS-1$
	private static final Templates cachedMergeXsl = SchematronPipeline.createCachedTransform(mergeXsl);

	private PolicyChecker() {

	}

	public static void insertPolicyReport(final File policyReport, final File mrrReport,
			final OutputStream mergedReport) throws VeraPDFException {
		try {
			Transformer transformer = cachedMergeXsl.newTransformer();
			transformer.setParameter("policyResultPath", policyReport.getAbsolutePath()); //$NON-NLS-1$
			transformer.transform(new StreamSource(mrrReport), new StreamResult(mergedReport));
			return;
		} catch (TransformerException excep) {
			throw new VeraPDFException("Problem merging XML files.", excep);
		}
	}

	public static void applyPolicy(final File policy, final InputStream xmlReport, final OutputStream policyReport)
			throws VeraPDFException {
		// Get the file extension
		String ext = getExtensionFromFileName(policy.getName());
		if (!isAllowedExtension(ext)) {
			throw new VeraPDFException("Policy file extension must be one of " + schematronExt + ", " + xslExtension //$NON-NLS-2$
					+ ", or " + xsltExtension);
		}
		boolean isXsl = !ext.equalsIgnoreCase(schematronExt);
		try (FileInputStream fis = new FileInputStream(policy)) {
			applyPolicy(fis, xmlReport, policyReport, isXsl);
		} catch (IOException excep) {
			throw new VeraPDFException("IOException applying policy file " + policy.getAbsolutePath(), excep);
		}
	}

	public static boolean isFilenameAllowedExtension(final String filename) {
		return isAllowedExtension(getExtensionFromFileName(filename));
	}

	public static boolean isAllowedExtension(final String ext) {
		return allowedExtensions.contains(ext);
	}

	public static void applyPolicy(final InputStream policy, final InputStream xmlReport,
			final OutputStream policyReport, boolean isXsl) throws VeraPDFException {
		try {
			if (isXsl) {
				applySchematronXsl(policy, xmlReport, policyReport);
			} else {
				applyRawSchematron(policy, xmlReport, policyReport);
			}
		} catch (IOException | TransformerException excep) {
			throw new VeraPDFException("Exception when applying policy file.", excep);
		}
	}

	private static void applyRawSchematron(final InputStream rawSchematron, final InputStream xmlReport,
			final OutputStream policyReport) throws TransformerException, IOException {
		File schemaXsl = createSchematronXslFile(rawSchematron);
		try (FileInputStream fis = new FileInputStream(schemaXsl)) {
			applySchematronXsl(fis, xmlReport, policyReport);
		}
	}

	private static File createSchematronXslFile(final InputStream rawSchematron)
			throws TransformerException, IOException {
		File resXsl = File.createTempFile("veraPDF_", "SchXsl"); //$NON-NLS-1$ //$NON-NLS-2$
		try (FileOutputStream fos = new FileOutputStream(resXsl)) {
			SchematronPipeline.processSchematron(rawSchematron, fos);
		}
		return resXsl;
	}

	private static void applySchematronXsl(final InputStream schematronXsl, final InputStream xmlReport,
			final OutputStream policyReport) throws TransformerException {
		Transformer transformer = factory.newTransformer(new StreamSource(schematronXsl));
		transformer.transform(new StreamSource(xmlReport), new StreamResult(policyReport));
	}

	private static String getExtensionFromFileName(final String filename) {
		if (!filename.contains(".")) { //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
		String[] nameParts = filename.split(extensionRegEx);
		if (nameParts.length > 0) {
			return nameParts[nameParts.length - 1];
		}
		return ""; //$NON-NLS-1$
	}
}
