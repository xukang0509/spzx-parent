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
          <el-option v-for="item in category1IdList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="queryParams.category2Id" @change="selectCategory2" class="m-2" placeholder="二级分类" size="small" style="width: 32%; margin-right: 5px">
          <el-option v-for="item in category2IdList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="queryParams.category3Id" class="m-2" placeholder="三级分类" size="small" style="width: 32%">
          <el-option v-for="item in category3IdList" :key="item.id" :label="item.name" :value="item.id" />
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

            </el-table>
          </el-form-item>
        </div>
      </el-form>
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
import {getProductList, updateAuditStatus, updateStatus} from "@/api/product/product.js";
import {getProductUnitAll} from "@/api/base/productUnit.js";
import {getToken} from "@/utils/auth.js";
import {getCategorySpecAll} from "@/api/product/productSpec.js";

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

/* 表单重置 */
function reset() {
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
  category2IdList.value = [];
  category3IdList.value = [];
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

/* 规格变化，商品规格sku变化处理，取规格属性值，笛卡尔积生成sku */
function handleSpecValueChange() {

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
  const { data } = await getCategoryBrandAll(form.value.category3Id)
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
const category1IdList = ref([]);
const category2IdList = ref([]);
const category3IdList = ref([]);
async function getTreeSelectCategoryList(parentId, level) {
  const {data} = await treeSelect(parentId);
  if (level == 1) {
    category1IdList.value = data;
  } else if (level == 2) {
    category2IdList.value = data;
  } else if (level == 3){
    category3IdList.value = data;
  }
}
function selectCategory1() {
  category2IdList.value = [];
  category3IdList.value = [];
  getTreeSelectCategoryList(queryParams.value.category1Id, 2);
}
function selectCategory2() {
  category3IdList.value = [];
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
const categoryIdList = ref([]);
// 处理分类change事件
const handleCategoryChange = () => {
  if (categoryIdList.value.length == 3) {
    form.value.category1Id = categoryIdList.value[0]
    form.value.category2Id = categoryIdList.value[1]
    form.value.category3Id = categoryIdList.value[2]
    getCategoryBrand()
    getCategorySpec()
  }
  console.log(categoryIdList.value)
}

onMounted(() => {
  getList();
  getBrandAllList();
  getProductUnitList();
  getTreeSelectCategoryList(0,1);
})
</script>

<style scoped>

</style>