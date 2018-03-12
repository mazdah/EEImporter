package com.innotree.smartkms.dataanalysis.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@EnableAutoConfiguration
public class DataRegistController {
	
	Logger logger = LoggerFactory.getLogger(DataRegistController.class);

	/**
	 * 데이터 등록 - 엑셀 파일로 작성된 데이터 등록
	 */
	
	@RequestMapping(value="/fileupload", method = RequestMethod.POST)
	@ResponseBody
	public synchronized Map<String, Object> fileUpload(@RequestParam("indexName") String indexName,
													  @RequestParam("type") String type,
													  @RequestParam("files") MultipartFile[] files) {
		Map <String, Object> resultMap = new HashMap <String, Object>();
		
		int fileCnt = files.length;
		for (MultipartFile file : files) {
			// 1. 저장할 디렉토리 생성
			// 2.파일 저장 : file.transgerTo (저장할 디렉토리)
		}
		
		return resultMap;
	}
}
