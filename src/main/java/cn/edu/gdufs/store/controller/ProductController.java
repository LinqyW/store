package cn.edu.gdufs.store.controller;

import cn.edu.gdufs.store.entity.Product;
import cn.edu.gdufs.store.service.ProductService;
import cn.edu.gdufs.store.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description:关于产品的控制器类
 */
@RestController
@Api(value = "v1", tags = "产品模块接口")
@RequestMapping("products")
public class ProductController extends BaseController {
    @Autowired
    private ProductService productService;

    /**
     * 查询前三名热销商品
     *
     * @return
     */
    @GetMapping("hot_list")
    @ApiOperation(value = "查询前三名热销商品", notes = "查询前三名热销商品")
    public JsonResult<List<Product>> getHotList() {
        List<Product> data = productService.findHotList();
        return new JsonResult<List<Product>>(OK, data);
    }

    /**
     * 根据商品id查询商品详情
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据商品id查询商品详情", notes = "根据商品id查询商品详情")
    public JsonResult<Product> getById(@PathVariable("id") Integer id) {
        // 调用业务对象执行获取数据
        Product data = productService.findById(id);
        // 返回成功和数据
        return new JsonResult<Product>(OK, data);
    }
}
