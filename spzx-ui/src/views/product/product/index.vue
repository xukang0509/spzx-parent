<template>
  <div class="app-container">
    <!-- 查询 -->
    <el-form :model="queryParams" ref="queryRef" v-show="showSearch" :inline="true" label-width="68px">
      <el-form-item label="商品名称" props="name">
        <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item label="品牌">
        <el-select v-model="queryParams.brandId" class="m-2" placeholder="选择品牌" size="small" style="width: 100%">
          <el-option v-for="item in brandList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="queryParams.category1Id" @change="selectCategory1" class="m-2" placeholder="一级分类" size="small" style="width: 32%; margin-right: 5px">
          <el-option v-for="item in category1List" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="queryParams.category2Id" @change="selectCategory2" class="m-2" placeholder="二级分类" size="small" style="width: 32%; margin-right: 5px">
          <el-option v-for="item in category2List" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="queryParams.category3Id" class="m-2" placeholder="三级分类" size="small" style="width: 32%">
          <el-option v-for="item in category3List" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" id="reset-all" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 功能按钮栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据展示表格 -->
    <el-table v-model="loading" :data="productList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" prop="id" width="60"/>
      <el-table-column label="轮播图" prop="sliderUrls" width="200" #default="scope">
        <div style="height: 50px;float: left;">
          <img v-for="(item, index) in scope.row.sliderUrlList" :key="index" :src="item" width="50" />
        </div>
      </el-table-column>
      <el-table-column label="商品名称" prop="name" width="160"/>
      <el-table-column label="品牌" prop="brandName" />
      <el-table-column label="一级分类" prop="category1Name" />
      <el-table-column label="二级分类" prop="category2Name" />
      <el-table-column label="三级分类" prop="category3Name"/>
      <el-table-column label="计量单位" prop="unitName"/>
      <el-table-column label="状态" prop="status" #default="scope">
        {{ scope.row.status == 0 ? '未上架' : (scope.row.status == 1 ? '上架' : '下架') }}
      </el-table-column>
      <el-table-column label="审核状态" prop="auditStatus" #default="scope">
        {{ scope.row.auditStatus == 0 ? '未审核' : (scope.row.auditStatus == 1 ? '通过' : '驳回') }}
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250">
        <template #default="scope">
          <el-button v-if="scope.row.auditStatus == 0" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-if="scope.row.auditStatus == 0" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-button v-if="scope.row.auditStatus == 0" link type="success" @click="handleAudit(scope.row.id, 1)">通过</el-button>
          <el-button v-if="scope.row.auditStatus == 0" link type="success" @click="handleAudit(scope.row.id, -1)">驳回</el-button>
          <el-button v-if="scope.row.auditStatus == 1 && (scope.row.status == 0 || scope.row.status == -1)"
                     link type="primary"  @click="handleUpdateStatus(scope.row.id, 1)">上架</el-button>
          <el-button v-if="scope.row.auditStatus == 1 && scope.row.status == 1"
                     link type="primary" plain @click="handleUpdateStatus(scope.row.id, -1)">下架</el-button>
        </template>
      </el-table-column>
    </el-table>


    <!-- 添加或修改商品对话框 -->
    <el-dialog :title="title" v-model="open" width="60%" append-to-body>
      <el-steps :active="activeIndex" finish-status="success">
        <el-step title="Step 1" description="商品基本信息" />
        <el-step title="Step 2" description="商品SKU信息" />
        <el-step title="Step 3" description="商品详情信息" />
      </el-steps>
      <el-form ref="productRef" :model="form" :rules="rules" label-width="80px" style="margin-top: 20px">
        <el-divider/>
        <div v-if="activeIndex == 0">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="分类" prop="category3Id">
            <el-cascader :props="categoryProps" v-model="categoryIdList" @change="handleCategoryChange"/>
          </el-form-item>
          <el-form-item label="品牌" prop="brandId">
            <el-select v-model="form.brandId" class="m-2" placeholder="选择品牌">
              <el-option v-for="item in categoryBrandList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="计量单位" prop="unitName">
            <el-select v-model="form.unitName" class="m-2" placeholder="计量单位">
              <el-option v-for="item in productUnitList" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="轮播图" prop="sliderUrlList">
            <el-upload
                v-model:file-list="sliderTempUrlList"
                :action="imgUpload.url"
                :headers="imgUpload.headers"
                list-type="picture-card"
                multiple
                :on-success="handleAvatarSuccess"
                :on-remove="handleRemove"
            >
              <el-icon><Plus/></el-icon>
            </el-upload>
          </el-form-item>
        </div>
        <div v-if="activeIndex == 1">
          <el-form-item label="选择规格">
            <el-select :disabled="form.id != null" v-model="form.specValue" class="m-2" placeholder="选择规格" size="default" @change="handleSpecValueChange">
              <el-option v-for="item in specList" :key="item.specValue" :label="item.specName" :value="item.specValue" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品SKU">
            <el-table :data="form.productSkuList" border style="width: 100%">
              <el-table-column prop="skuSpec" label="规格" width="180" />
              <el-table-column label="图片" #default="scope" width="80">
                  <el-upload
                      class="avatar-uploader"
                      :action="imgUpload.url"
                      :headers="imgUpload.headers"
                      :show-file-list="false"
                      :on-success="(response, uploadFile, fileList) =>handleSkuSuccess(response, uploadFile, fileList, scope.row)">
                    <img v-if="scope.row.thumbImg" :src="scope.row.thumbImg" class="avatar" width="50" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
              </el-table-column>
              <el-table-column label="售价" #default="scope">
                <el-input v-model="scope.row.salePrice" />
              </el-table-column>
              <el-table-column label="市场价" #default="scope">
                <el-input v-model="scope.row.marketPrice" />
              </el-table-column>
              <el-table-column label="成本价" #default="scope">
                <el-input v-model="scope.row.costPrice" />
              </el-table-column>
              <el-table-column label="库存数" #default="scope">
                <el-input v-model="scope.row.stockNum" />
              </el-table-column>
              <el-table-column label="重量" #default="scope">
                <el-input v-model="scope.row.weight" />
              </el-table-column>
              <el-table-column label="体积" #default="scope">
                <el-input v-model="scope.row.volume" />
              </el-table-column>
            </el-table>
          </el-form-item>
        </div>
        <div v-if="activeIndex == 2">
          <el-form-item label="详情图片">
            <el-upload
                v-model:file-list="detailsImageTempUrlList"
                :action="imgUpload.url"
                :headers="imgUpload.headers"
                list-type="picture-card"
                multiple
                :on-success="handleDetailsSuccess"
                :on-remove="handleDetailsRemove"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </div>
        <div v-if="activeIndex == 3">
          <div style="font-size: large;margin: 30px 30px;">提交成功</div>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" @click="submitForm(-1)" :disabled="activeIndex == 0">上一步</el-button>
          <el-button type="primary" @click="submitForm(1)">{{ activeIndex < 2  ? '下一步' : '提交' }}</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分页组件 -->
    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />
  </div>
</template>

<script setup name="Product">
import {getBrandAll} from "@/api/product/brand.js";
import {treeSelect} from "@/api/product/category.js";
import {getProductList, updateAuditStatus, updateStatus, addProduct, updateProduct, getProduct, deleteProduct} from "@/api/product/product.js";
import {getProductUnitAll} from "@/api/base/productUnit.js";
import {getToken} from "@/utils/auth.js";
import {getCategorySpecAll} from "@/api/product/productSpec.js";
import {getBrandListByCategoryId} from "@/api/product/categoryBrand.js";

const {proxy} = getCurrentInstance();

// 定义分页列表数据模型
const productList = ref([]);
// 定义列表总记录数模型
const total = ref(0);
// 加载数据时显示的动态控制模型
const loading = ref(true);
// 新增与修改弹出层控制模型
const open = ref(false);
// 新增与修改弹出层标题模型
const title = ref("");
// 定义批量操作id列表模型
const ids = ref([]);
// 定义单选控制模型
const single = ref(true);
// 定义多选控制模型
const multiple = ref(true);
// 定义隐藏搜索控制模型
const showSearch = ref(true);
// 提交按钮
const activeIndex = ref(0)

const data = reactive({
  // 定义搜索模型
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    brandId: null,
    category1Id: null,
    category2Id: null,
    category3Id: null,
    status: null,
    auditStatus: null
  },
  imgUpload: {
    headers: {
      Authorization: "Bearer " + getToken()
    },
    url: import.meta.env.VITE_APP_BASE_API + '/file/upload'
  },
  form: {},
  rules: {
    name: [
      { required: true, message: "商品名称不能为空", trigger: "change" }
    ],
    category3Id: [
      { required: true, message: "商品分类不能为空", trigger: "change" }
    ],
    brandId: [
      { required: true, message: "品牌不能为空", trigger: "change" }
    ],
    unitName: [
      { required: true, message: "商品单位不能为空", trigger: "change" }
    ],
    sliderUrlList: [
      { required: true, message: "轮播图不能为空", trigger: "change" }
    ]
  }
});
const {queryParams, form, rules, imgUpload} = toRefs(data);

/* 查询商品列表 */
function getList(){
  loading.value = true;
  getProductList(queryParams.value).then(response => {
    productList.value = response.rows;
    total.value = response.total;
    productList.value.forEach(item => {
      item.sliderUrlList = item.sliderUrls.split(',');
    })
    loading.value = false;
  })
}

/* 添加按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增商品";
}

/* 更新按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getProduct(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "更新商品";

    // 分类赋值
    categoryIdList.value = [
        form.value.category1Id,
        form.value.category2Id,
        form.value.category3Id
    ]
    // 轮播图赋值
    form.value.sliderUrlList = form.value.sliderUrls.split(',');
    form.value.sliderUrlList.forEach(url => {
      sliderTempUrlList.value.push({url: url});
    })
    // 处理详情图片
    form.value.detailsImageUrlList.forEach(url => {
      detailsImageTempUrlList.value.push({ url: url })
    })
    // 加载分类品牌
    getCategoryBrand()
    // 加载分类规格
    getCategorySpec()
  })
}

/* 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除商品编号为"' + _ids + '"的数据项？').then(function () {
    return deleteProduct(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

/* 表单重置 */
function reset() {
  activeIndex.value = 0;
  sliderTempUrlList.value = [];
  detailsImageTempUrlList.value = [];
  categoryIdList.value = [];
  categoryBrandList.value = [];
  specList.value = [];
  form.value = {
    id: null,
    name: null,
    brandId: null,
    category1Id: null,
    category2Id: null,
    category3Id: null,
    unitName: null,
    sliderUrls: null,
    specValue: null,
    status: null,
    auditStatus: null,
    auditMessage: null,
    sliderUrlList: [],
    productSkuList: [],
    detailsImageUrlList: []
  };
  proxy.resetForm("productRef");
}

/* 搜索、重置搜索 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
function resetQuery() {
  proxy.resetForm("queryRef");
  category2List.value = [];
  category3List.value = [];
  queryParams.value.category1Id = null;
  queryParams.value.category2Id = null;
  queryParams.value.category3Id = null;
  queryParams.value.brandId = null;
  handleQuery();
}

/* 商品审核、更新上下架信息 */
function handleAudit(id, auditStatus) {
  const msg = auditStatus == 1 ? '是否确认审批通过商品编号为"' + id + '"的数据项？' : '是否确认审批驳回商品编号为"' + id + '"的数据项？';
  proxy.$modal.confirm(msg).then(() => {
    return updateAuditStatus(id, auditStatus);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("操作成功");
  }).catch(()=>{});
}
function handleUpdateStatus(id, status) {
  const msg = status == 1 ? '是否确认上架商品编号为"' + id + '"的数据项？' : '是否确认下架商品编号为"' + id + '"的数据项？'
  proxy.$modal.confirm(msg).then(() => {
    return updateStatus(id, status);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("操作成功");
  }).catch(()=>{});
}

/* 多选框按钮处理 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/* 上传商品轮播图图片 */
const sliderTempUrlList = ref([]);
function handleAvatarSuccess(response, uploadFile) {
  if (response.code == 200) {
    form.value.sliderUrlList.push(response.data.url);
    console.log('------handleAvatarSuccess start--------')
    console.log(form.value.sliderUrlList)
    console.log(sliderTempUrlList.value)
    console.log('------handleAvatarSuccess end--------')
  } else {
    proxy.$modal.msgError(response.msg);
  }
}
function handleRemove(uploadFile, uploadFiles) {
  console.log('------handleRemove start--------')
  console.log(uploadFile, uploadFiles);
  form.value.sliderUrlList.splice(form.value.sliderUrlList.indexOf(uploadFiles.url), 1);
  console.log(form.value.sliderUrlList);
  console.log('------handleRemove end--------')
}

// 上传商品详情图片
const detailsImageTempUrlList = ref([])
const handleDetailsRemove = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles)
  form.value.detailsImageUrlList.splice(
      form.value.detailsImageUrlList.indexOf(uploadFile.url),
      1
  )
  console.log(form.value.detailsImageUrlList)
}
const handleDetailsSuccess = (response, uploadFile) => {
  console.log(response)
  form.value.detailsImageUrlList.push(response.data.url)
  console.log(form.value.detailsImageUrlList)
}

/* 规格变化，商品规格sku变化处理，取规格属性值，笛卡尔积生成sku */
function handleSpecValueChange() {
  form.value.productSkuList = []
  let specValueList = JSON.parse(form.value.specValue);
  console.log(specValueList);

  let array = [];
  specValueList.forEach(item => {
    let res = []
    item.valueList.map(x => {
      res.push(x)
    })
    array.push(res);
  })
  // 取数组的笛卡尔积
  let result = array.reduce(
      (a, b, c) => {
        let res = []
        a.map(x => {
          b.map(y => {
            res.push([...x, y])
          })
        })
        return res;
      },
      [[]]
  )
  console.log(result)
  result.forEach(item => {
    form.value.productSkuList.push({
      skuSpec: item.join(' + '),
      skuSpecList: item,
      price: 0,
    })
  })
}

//sku图片
const handleSkuSuccess = (response, uploadFile, fileList, row) => {
  console.log(response)
  console.log(uploadFile)
  row.thumbImg = response.data.url;
}

/* 根据分类ID加载商品规格 */
const specList = ref([]);
function getCategorySpec() {
  if (form.value.category3Id == '') {
    proxy.$modal.msgError("请选择分类");
    return;
  }
  getCategorySpecAll(form.value.category3Id).then(response => {
    specList.value = response.data;
  })
}

/* 根据分类id加载品牌 */
const categoryBrandList = ref([])
const getCategoryBrand = async () => {
  if (form.value.category3Id == '') {
    proxy.$modal.msgError("请选择分类");
    return
  }
  const { data } = await getBrandListByCategoryId(form.value.category3Id)
  categoryBrandList.value = data
}

/* 选择器---加载所有品牌 */
const brandList = ref([])
function getBrandAllList() {
  getBrandAll().then(response => {
    brandList.value = response.data
  })
}

/* 选择器---加载商品单位 */
const productUnitList = ref([])
function getProductUnitList() {
  getProductUnitAll().then(response => {
    productUnitList.value = response.data
  })
}

/* 级联分类选择器处理 */
const category1List = ref([]);
const category2List = ref([]);
const category3List = ref([]);
async function getTreeSelectCategoryList(parentId, level) {
  const {data} = await treeSelect(parentId);
  if (level == 1) {
    category1List.value = data;
  } else if (level == 2) {
    category2List.value = data;
  } else if (level == 3){
    category3List.value = data;
  }
}
function selectCategory1() {
  category2List.value = [];
  category3List.value = [];
  getTreeSelectCategoryList(queryParams.value.category1Id, 2);
}
function selectCategory2() {
  category3List.value = [];
  getTreeSelectCategoryList(queryParams.value.category2Id, 3);
}

/* 级联选择器---分类 */
const props = {
  lazy: true,
  value: 'id',
  label: 'name',
  leaf: 'leaf',
  async lazyLoad(node, resolve) { // 加载数据的方法
    if (typeof node.value == 'undefined') node.value = 0
    const {data} = await treeSelect(node.value);
    // hasChildren判断是否有子节点
    data.forEach(function (item) {
      item.leaf = !item.hasChildren
    });
    resolve(data);
  }
};
const categoryProps = ref(props);
// 处理分类change事件
const categoryIdList = ref([]);
const handleCategoryChange = () => {
  if (categoryIdList.value.length == 3) {
    form.value.category1Id = categoryIdList.value[0]
    form.value.category2Id = categoryIdList.value[1]
    form.value.category3Id = categoryIdList.value[2]
    form.value.specValue = null;
    form.value.productSkuList = [];
    getCategoryBrand();
    getCategorySpec();
  }
  console.log(categoryIdList.value)
}

/* 提交按钮 */
function submitForm(index) {
  proxy.$refs["productRef"].validate(valid => {
    if (valid) {
      activeIndex.value = activeIndex.value + index;
      //轮播图临时地址重新赋值，否则新上传的图片不显示
      sliderTempUrlList.value = [];
      form.value.sliderUrlList.forEach(url => {
        sliderTempUrlList.value.push({url: url});
      })

      //详情图片临时地址重新赋值，否则新上传的图片不显示
      detailsImageTempUrlList.value = []
      form.value.detailsImageUrlList.forEach(url => {
        detailsImageTempUrlList.value.push({url: url})
      })

      if (activeIndex.value == 3) {
        form.value.sliderUrls = form.value.sliderUrlList.join(',')
        if (form.value.id != null) {
          updateProduct(form.value).then(response => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          });
        } else {
          addProduct(form.value).then(response => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          });
        }
      }
    }
  });
}

/* 取消表单按钮 */
function cancel() {
  open.value = false;
  reset();
}

onMounted(() => {
  getList();
  getBrandAllList();
  getProductUnitList();
  getTreeSelectCategoryList(0,1);
})
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 70px;
  height: 70px;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 50px;
  height: 50px;
  text-align: center;
}
</style>