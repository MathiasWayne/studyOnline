package com.xuecheng.cms;

import com.xuecheng.framework.domain.system.SysDictionary;

public interface CmsSysDictionaryControllerApi {
   SysDictionary findSysDictionaryByType(String dType);
}
