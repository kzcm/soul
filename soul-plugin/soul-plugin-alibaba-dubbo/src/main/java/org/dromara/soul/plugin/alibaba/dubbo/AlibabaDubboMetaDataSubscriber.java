/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.soul.plugin.alibaba.dubbo;

import com.google.common.collect.Maps;
import org.dromara.soul.cache.api.MetaDataSubscriber;
import org.dromara.soul.common.dto.MetaData;
import org.dromara.soul.common.enums.RpcTypeEnum;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

public class AlibabaDubboMetaDataSubscriber implements MetaDataSubscriber {
    
    private static final ConcurrentMap<String, MetaData> META_DATA = Maps.newConcurrentMap();
    
    @Override
    public void onSubscribe(MetaData metaData) {
        if (RpcTypeEnum.DUBBO.getName().equals(metaData.getRpcType())) {
            MetaData exist = META_DATA.get(metaData.getPath());
            if (Objects.isNull(exist) || Objects.isNull(ApplicationConfigCache.getInstance().get(exist.getServiceName()).isInit())) {
                //第一次初始化
                ApplicationConfigCache.getInstance().initRef(metaData);
            } else {
                if (!exist.getServiceName().equals(metaData.getServiceName()) || !exist.getRpcExt().equals(metaData.getRpcExt())) {
                    //有更新
                    ApplicationConfigCache.getInstance().build(metaData);
                }
            }
            META_DATA.put(metaData.getPath(), metaData);
        }
    }
}