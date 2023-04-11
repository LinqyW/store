package cn.edu.gdufs.store.service.Impl;

import cn.edu.gdufs.store.entity.District;
import cn.edu.gdufs.store.mapper.DistrictMapper;
import cn.edu.gdufs.store.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:处理省/市/区数据的业务层实现类
 */
@Service
public class DistrictServiceImpl implements DistrictService {
    @Autowired
    private DistrictMapper districtMapper;

    @Override
    public List<District> getByParent(String parent) {
        List<District> list = districtMapper.findByParent(parent);
        //因为在配置文件中设置了返回响应时不返回空值，所以将id和parent字段的值设为null即可不返回这两个字段的结果
        for (District district : list) {
            district.setId(null);
            district.setParent(null);
        }
        return list;
    }

    @Override
    public String getNameByCode(String code) {
        return districtMapper.findNameByCode(code);
    }
}
