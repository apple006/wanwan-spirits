package org.wanwanframework.file.spiritgirl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.wanwanframework.file.map.FilterUtil;
import org.wanwanframework.file.map.LineTool;
import org.wanwanframework.file.map.MappingUtil;
import org.wanwanframwork.file.FileReader;
import org.wanwanframwork.file.FileUtil;
import org.wanwanframwork.file.Log;

/**
 * 分发格式： file->path |content
 * 
 * fileList->list<file> 基础配置文件为路径配置文件，指定系统的起点 参数配置文件+模板配置文件为输入配置
 */
public class SpiritgirlController {

	private Map<String, String> config;// 基础配置文件：指定参数配置+模板配置
	private Map<String, String> param;
	private Map<String, String> templateMap = new HashMap<String, String>();

	public SpiritgirlController() {
		Map<String, String> urlMap = LineTool.getLine("./src/main/resources/spirit/url.txt", ":\t");
		String path = null;
		for(String key : urlMap.keySet()) {
			if(key.indexOf("path") > -1) {
				path = urlMap.get("path");
			}
		}
		config = LineTool.getLine(path, ":\t");
	}

	public void init() {
		Map<String, String>[] mapArray = MappingUtil.getMapping(config.get("param"), ":\t");

		templateMap = FileReader.loads(config.get("template"));
		if (mapArray.length > 0) {
			param = mapArray[0];
			for (int i = 0; i < mapArray.length - 1; i++) {
				processFileStructure(mapArray[i + 1]);
				processTemplate(mapArray[i + 1]);
			}
		}
	}

	/**
	 * 最后处理模板文件:得到内容后直接放到路径下面去修改
	 */
	private void processTemplate(Map<String, String> map) {
		String content;
		for (String key : templateMap.keySet()) {
			content = templateMap.get(key);
			content = FilterUtil.processFilter(content, param);
			modifyFile(key, map, content);
		}
	}

	/**
	 * 修改文件：通过匹配文件名关键字，把内容写到空文件中
	 * 
	 * @param templateKey
	 * @param map
	 * @param content
	 */
	private void modifyFile(String templateKey, Map<String, String> map, String content) {
		for (String fileKey : map.keySet()) {
			if (fileKey.indexOf(templateKey) >= 0) {
				String path = map.get(fileKey);
				try {
					FileUtil.createFile(path, content);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 处理文件系统
	 * 
	 * @param map
	 */
	private void processFileStructure(Map<String, String> map) {
		String value;
		for (String key : map.keySet()) {
			value = map.get(key);
			value = FilterUtil.processFilter(value, param);
			map.put(key, value); // 修改键值对
			makeFile(key, value);
			Log.log("key:" + key + ", value:" + value);
		}
	}

	/**
	 * 处理单个文件或者目录
	 * 
	 * @param key
	 * @param value
	 */
	private void makeFile(String key, String value) {
		if (key.contains("@")) {
			if (key.indexOf("@file") >= 0) {
				try {
					FileUtil.makeFile(value);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (key.indexOf("@folder") >= 0) {
				FileUtil.makeFolder(value);
			}
		}
	}

	public static void main(String[] args) {
		SpiritgirlController controller = new SpiritgirlController();
		controller.init();
	}
}
