package com.atcwl.agent.data;


import com.atcwl.common.interfaces.DataCollection;
import com.atcwl.common.spi.ExtensionLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-16 23:15
 **/
public class DataCollectionCache {

    public static List<DataCollection> DATA_COLLECTION_MAP = new ArrayList<>(4);

    static {
        Map<String, Class<?>> extensionClassesCache = ExtensionLoader.getLoader(DataCollection.class).getExtensionClassesCache();
        for (String s : extensionClassesCache.keySet()) {
            try {
                DATA_COLLECTION_MAP.add((DataCollection) extensionClassesCache.get(s).newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
