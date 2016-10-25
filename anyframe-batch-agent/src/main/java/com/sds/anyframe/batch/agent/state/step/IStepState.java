/*
 * Copyright 2010-2012 Samsung SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sds.anyframe.batch.agent.state.step;


import com.sds.anyframe.batch.agent.model.Step;

/**
 * 
 * 
 * @author Hyoungsoon Kim
 */
public interface IStepState {

	enum STATES {
		READY, RUNNING, COMPLETED, STOPPED, FAILED
	}
	
	Step startStep(Step step) throws Exception;

	void completeStep(Step step) throws Exception;

	void stepExecuted(Step step); 
	
	void stopStep(Step step) throws Exception;
	
	void failStep(Step step) throws Exception;
	
	STATES getState();
}
