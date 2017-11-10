/*******************************************************************************
 * Copyright (c) 2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.java.beans.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.SymbolInformation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ide.vscode.boot.java.Annotations;
import org.springframework.ide.vscode.boot.java.beans.BeansSymbolProvider;
import org.springframework.ide.vscode.boot.java.beans.ComponentSymbolProvider;
import org.springframework.ide.vscode.boot.java.beans.test.SpringIndexerHarness.TestSymbolInfo;
import org.springframework.ide.vscode.boot.java.handlers.SymbolProvider;
import org.springframework.ide.vscode.boot.java.utils.SpringIndexer;
import org.springframework.ide.vscode.commons.languageserver.java.JavaProjectFinder;
import org.springframework.ide.vscode.project.harness.BootLanguageServerHarness;
import org.springframework.ide.vscode.project.harness.ProjectsHarness;

/**
 * @author Martin Lippert
 */
public class SpringIndexerBeansTest {

	private Map<String, SymbolProvider> symbolProviders;
	private BootLanguageServerHarness harness;
	private JavaProjectFinder projectFinder;

	@Before
	public void setup() throws Exception {
		symbolProviders = new HashMap<>();
		symbolProviders.put(Annotations.BEAN, new BeansSymbolProvider());
		symbolProviders.put(Annotations.COMPONENT, new ComponentSymbolProvider());

		harness = BootLanguageServerHarness.builder().build();
		projectFinder = harness.getProjectFinder();
		harness.intialize(new File(ProjectsHarness.class.getResource("/test-projects/test-annotation-indexing-beans/").toURI()));
	}

	@Test
	public void testScanSimpleConfigurationClass() throws Exception {
		SpringIndexerHarness indexer = new SpringIndexerHarness(harness.getServer(), projectFinder, symbolProviders);
		File directory = new File(ProjectsHarness.class.getResource("/test-projects/test-annotation-indexing-beans/").toURI());
		indexer.initialize(directory.toPath());

		String uriPrefix = "file://" + directory.getAbsolutePath();
		indexer.assertDocumentSymbols(uriPrefix + "/src/main/java/org/test/SimpleConfiguration.java",
				symbol("@Configuration", "@Configuration"),
				symbol("@Bean", "@+ 'simpleBean' (@Bean) BeanClass")
		);
	}

	@Test
	public void testScanSpecialConfigurationClass() throws Exception {
		SpringIndexerHarness indexer = new SpringIndexerHarness(harness.getServer(), projectFinder, symbolProviders);
		File directory = new File(ProjectsHarness.class.getResource("/test-projects/test-annotation-indexing-beans/").toURI());
		indexer.initialize(directory.toPath());

		String uriPrefix = "file://" + directory.getAbsolutePath();
		String docUri = uriPrefix + "/src/main/java/org/test/SpecialConfiguration.java";
		indexer.assertDocumentSymbols(docUri,
				symbol("@Configuration", "@Configuration"),

				// @Bean("implicitNamedBean")
				symbol("implicitNamedBean", "@+ 'implicitNamedBean' (@Bean) BeanClass"),

				// @Bean(value="valueBean")
				symbol("valueBean", "@+ 'valueBean' (@Bean) BeanClass"),

				// @Bean(value= {"valueBean1", "valueBean2"})
				symbol("valueBean1", "@+ 'valueBean1' (@Bean) BeanClass"),
				symbol("valueBean2", "@+ 'valueBean2' (@Bean) BeanClass"),

				// @Bean(name="namedBean")
				symbol("namedBean", "@+ 'namedBean' (@Bean) BeanClass"),

				// @Bean(name= {"namedBean1", "namedBean2"})
				symbol("namedBean1", "@+ 'namedBean1' (@Bean) BeanClass"),
				symbol("namedBean2", "@+ 'namedBean2' (@Bean) BeanClass")
		);
	}

	@Test
	public void testScanSimpleFunctionBean() throws Exception {
		SpringIndexer indexer = new SpringIndexer(harness.getServer(), projectFinder, symbolProviders);
		File directory = new File(ProjectsHarness.class.getResource("/test-projects/test-annotation-indexing-beans/").toURI());
		indexer.initialize(directory.toPath());

		String uriPrefix = "file://" + directory.getAbsolutePath();
		List<? extends SymbolInformation> symbols = indexer.getSymbols(uriPrefix + "/src/main/java/org/test/FunctionClass.java");
		assertEquals(2, symbols.size());
		assertTrue(containsSymbol(symbols, "@> 'uppercase' (@Bean) Function<String,String>", uriPrefix + "/src/main/java/org/test/FunctionClass.java", 10, 1, 10, 6));
	}

	@Test
	public void testScanSimpleComponentClass() throws Exception {
		SpringIndexer indexer = new SpringIndexer(harness.getServer(), projectFinder, symbolProviders);
		File directory = new File(ProjectsHarness.class.getResource("/test-projects/test-annotation-indexing-beans/").toURI());
		indexer.initialize(directory.toPath());

		String uriPrefix = "file://" + directory.getAbsolutePath();
		List<? extends SymbolInformation> symbols = indexer.getSymbols(uriPrefix + "/src/main/java/org/test/SimpleComponent.java");
		assertEquals(1, symbols.size());
		assertTrue(containsSymbol(symbols, "@+ 'simpleComponent' (@Component) SimpleComponent", uriPrefix + "/src/main/java/org/test/SimpleComponent.java", 4, 0, 4, 10));
	}

	////////////////////////////////
	// harness code

	private boolean containsSymbol(List<? extends SymbolInformation> symbols, String name, String uri, int startLine, int startCHaracter, int endLine, int endCharacter) {
		for (Iterator<? extends SymbolInformation> iterator = symbols.iterator(); iterator.hasNext();) {
			SymbolInformation symbol = iterator.next();

			if (symbol.getName().equals(name)
					&& symbol.getLocation().getUri().equals(uri)
					&& symbol.getLocation().getRange().getStart().getLine() == startLine
					&& symbol.getLocation().getRange().getStart().getCharacter() == startCHaracter
					&& symbol.getLocation().getRange().getEnd().getLine() == endLine
					&& symbol.getLocation().getRange().getEnd().getCharacter() == endCharacter) {
				return true;
			}
 		}

		return false;
	}

	private TestSymbolInfo symbol(String coveredText, String label) {
		return new TestSymbolInfo(coveredText, label);
	}
}
