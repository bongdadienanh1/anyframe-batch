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

package com.sds.anyframe.batch.infra.support;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sds.anyframe.batch.config.BatchResource;
import com.sds.anyframe.batch.config.BatchResource.Mode;
import com.sds.anyframe.batch.core.item.support.AnyframeItemReader;
import com.sds.anyframe.batch.define.BatchDefine;
import com.sds.anyframe.batch.exception.BatchRuntimeException;
import com.sds.anyframe.batch.infra.delegate.MultiItemReader;
import com.sds.anyframe.batch.infra.file.ByteReader;
import com.sds.anyframe.batch.infra.file.ByteUpdater;
import com.sds.anyframe.batch.infra.file.ByteWriter;
import com.sds.anyframe.batch.vo.transform.Transform;
import com.sds.anyframe.batch.vo.transform.bytes.TransformByte;

/**                               
 * 								
 *                                
 * @author Hyoungsoon Kim         
 */								

public class ByteResourceFactoryBean extends AbstractResourceFactoryBean {

	private static final Log LOGGER = LogFactory.getLog(ByteResourceFactoryBean.class);
	
	public ByteResourceFactoryBean() {
		this.transformClass = TransformByte.class;
	}
	
	public Object getObject() throws Exception {
		String id      	   = resource.getId();
		String att_charset = resource.getAttribute(CHAR_SET);
		String att_lineSep = resource.getAttribute(LINE_SEPARATOR);
		String att_trim    = resource.getAttribute(TRIM);
		String att_buffer  = resource.getAttribute(BUFFER_SIZE);
		String att_delete  = resource.getAttribute(DELETE_EMPTY);
		String att_maxSize = resource.getAttribute(MAX_SIZE);
		String att_fixed   = resource.getAttribute(FIXED);
		
		boolean trimString    = BatchDefine.READER_SAM_TRIM; 
		boolean deleteEmpty   = BatchDefine.WRITER_DELETE_EMPTY;
		boolean fixedRow      = BatchDefine.READER_SAM_FIXED_ROW;
		int bufferSizeReader  = BatchDefine.READER_BUFFER_SIZE_DEFAULT_KB * 1024;
		int bufferSizeWriter  = BatchDefine.WRITER_BUFFER_SIZE_DEFAULT_KB * 1024;
		long maxFileSize      = BatchDefine.WRITER_FILE_MAX_SIZE_GB * (long)1024 * (long)1024 * (long)1024;

		Transform transform = this.transformClass.newInstance();
		Mode mode = resource.getMode();
		
		if(!StringUtils.isEmpty(att_trim))
			trimString = Boolean.parseBoolean(att_trim);
				
		if(!StringUtils.isEmpty(att_delete))
			deleteEmpty = Boolean.parseBoolean(att_delete);
		
		if(!StringUtils.isEmpty(att_fixed))
			fixedRow = Boolean.parseBoolean(att_fixed);
		
		if(mode == Mode.WRITE) {
			if(!StringUtils.isEmpty(att_maxSize)) {
				long fileSizeGB = 0;
				try {
					fileSizeGB = Long.parseLong(att_maxSize);
				} catch (NumberFormatException e) {
					throw new BatchRuntimeException(String.format("@maxsize should be digit. input value is %s", att_maxSize));
				}
				maxFileSize = fileSizeGB * (long)1024 * (long)1024 * (long)1024;
				LOGGER.debug(String.format("The maximum size of [%s] has been set to %,dGB", id, fileSizeGB));
			} else {
				LOGGER.debug(String.format("The maximum size of [%s] has been set to %,dGB by default", id, BatchDefine.WRITER_FILE_MAX_SIZE_GB));
			}
		}
		
		if(!StringUtils.isEmpty(att_buffer)) {
			bufferSizeReader = Integer.parseInt(att_buffer);
			bufferSizeWriter = Integer.parseInt(att_buffer);
		}
		
		// check charSet and set default
		if (StringUtils.isEmpty(att_charset)) {
			LOGGER.debug("@charset for resource [" + id
					+ "] is not defined. " + "The default ["
					+ BatchDefine.DEFAULT_ENCODING + "] is set");
			att_charset = BatchDefine.DEFAULT_ENCODING;
		}
		
		// check line separator and check default
		if(StringUtils.isEmpty(att_lineSep)) {
			LOGGER.debug("@linesep for resource [" + id + "] is not defined. " +
					"System default [" + LINE_SEPARATOR_SYSTEM_PRINT + "] is set");
			att_lineSep = LINE_SEPARATOR_SYSTEM;	// system default.
		} else {
			
			if(att_lineSep.compareTo("null") == 0)
				att_lineSep = null;
			else
				att_lineSep = this.getLineSeparator(att_lineSep);
		}
		
		switch(mode) {
		case WRITE:
			ByteWriter writer = new ByteWriter();
			
			writer.setTransform(transform);
			writer.setResource(resource);
			writer.setEncoding(att_charset);
			writer.setBufferSize(bufferSizeWriter);
			writer.setDeleteEmpty(deleteEmpty);
			writer.setLineSeparator(att_lineSep);
			writer.setItemCountRef(resource.getCountReference());
			writer.setMaxSize(maxFileSize);

			return writer;
			
		case UPDATE:
			ByteUpdater readwriter = new ByteUpdater();
			
			readwriter.setTransform(transform);
			readwriter.setResource(resource);
			readwriter.setEncoding(att_charset);
			readwriter.setBufferSize(bufferSizeReader);
			readwriter.setTrim(trimString);
			readwriter.setItemCountRef(resource.getCountReference());

			return readwriter;
			
		case READ:
			AnyframeItemReader itemReader = null;
			if(resource.hasChildren()) {
				MultiItemReader multiReader = new MultiItemReader();
				
				for(BatchResource childResource : resource.getChildResource()) {
					ByteReader reader = new ByteReader();
					
					reader.setTransform(transform);
					reader.setResource(childResource);
					reader.setEncoding(att_charset);
					reader.setBufferSize(bufferSizeReader);
					reader.setTrim(trimString);
					reader.setItemCountRef(childResource.getCountReference());
					reader.setFixed(fixedRow);
					reader.setSkipCRLF(att_lineSep != null);

					multiReader.addReader(reader);
				}
				itemReader = multiReader;
			} else {
				ByteReader reader = new ByteReader();
				
				reader.setTransform(transform);
				reader.setResource(resource);
				reader.setEncoding(att_charset);
				reader.setBufferSize(bufferSizeReader);
				reader.setTrim(trimString);
				reader.setItemCountRef(resource.getCountReference());
				reader.setFixed(fixedRow);
				reader.setSkipCRLF(att_lineSep != null);

				itemReader = reader;
			}
			return itemReader;
			
		default:
			throw new BatchRuntimeException("@type[" + mode + "] is invalid for SAM");
		}
	}

}
