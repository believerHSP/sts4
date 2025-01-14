/*******************************************************************************
 * Copyright (c) 2019, 2020 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.boot.java.livehover.v2;

/**
 * @author V Udayani 
 */
public class SpringProcessMemoryMetricsLiveData {
	
	private final ProcessType processType;
	private final String processName;
	private final String processID;
	
	private final LiveMemoryMetricsModel[] memoryMetrics;
	
	public SpringProcessMemoryMetricsLiveData(ProcessType processType, String processName, String processID, LiveMemoryMetricsModel[] memoryMetrics) {
		super();
		this.processType = processType;
		this.processName = processName;
		this.processID = processID;
		this.memoryMetrics = memoryMetrics;
		
	}
	
	public ProcessType getProcessType() {
		return processType;
	}

	public String getProcessName() {
		return this.processName;
	}

	public String getProcessID() {
		return this.processID;
	}
	
	public LiveMemoryMetricsModel[] getMemoryMetrics() {
		return this.memoryMetrics;
	}

}
