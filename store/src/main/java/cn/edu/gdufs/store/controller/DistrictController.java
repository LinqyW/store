package cn.edu.gdufs.store.controller;

import cn.edu.gdufs.store.entity.District;
import cn.edu.gdufs.store.service.DistrictService;
import cn.edu.gdufs.store.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description:地址相关的控制器类
 */
@RestController
@Api(value = "v1", tags = "地址模块接口")
@RequestMapping("/districts")
public class DistrictController extends BaseController {
    @Autowired
    private DistrictService districtService;

    /**
     * 获取全国所有省/某省所有市/某市所有区
     * @param parent
     * @return
     */
    @GetMapping({"", "/"})
    @ApiOperation(value = "获取全国所有省/某省所有市/某市所有区", notes = "获取全国所有省/某省所有市/某市所有区")
    public JsonResult<List<District>> getByParent(String parent) {
        List<District> data = districtService.getByParent(parent);
        return new JsonResult<>(OK, data);
    }
}
