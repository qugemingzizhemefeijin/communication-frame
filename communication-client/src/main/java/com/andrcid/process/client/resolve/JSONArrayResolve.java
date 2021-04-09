package com.andrcid.process.client.resolve;

import com.alibaba.fastjson.JSONArray;
import com.tigerjoys.communication.protocol.utility.FastJsonHelper;

public class JSONArrayResolve implements IResolve<JSONArray> {
	
	private static final JSONArray EMPTY_OBJECT = new JSONArray();

	@Override
	public Class<JSONArray> getResolveClass() {
		return JSONArray.class;
	}

	@Override
	public JSONArray resolve(String res) {
		if(res == null) {
			return EMPTY_OBJECT;
		}
		
		return FastJsonHelper.toJsonArray(res);
	}

}
