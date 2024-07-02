<template>
  <div class="app-container">
    <!-- 选择器 -->
    <el-form :model="queryParams" ref="queryRef" v-show="showSearch" :inline="true" label-width="68px">
      <el-form-item label="分类">
        <el-cascader :props="categoryProps" v-model="queryCategoryIdList" @change="handleCategoryChange" style="width: 100%" />
      </el-form-item>
      <el-form-item label="品牌">
        <el-select v-model="queryParams.brandId" class="m-2" placeholder="选择品牌" size="small" style="width: 100%">
          <el-option v-for="item in brandList" :key="item.id" :label="item.name" :value="item.id" />
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
        <el-button type="primary" plain icon="Plus" v-hasPermi="['product:categoryBrand:add']" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" v-hasPermi="['product:categoryBrand:edit']" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" v-hasPermi="['product:categoryBrand:remove']" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据展示表格 -->
    <el-table v-model="loading" :data="categoryBrandList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="分类名称" prop="categoryName"/>
      <el-table-column label="品牌名称" prop="brandName"/>
      <el-table-column label="品牌图标" prop="logo" #default="scope">
        <img :src="scope.row.logo" width="50"/>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" v-hasPermi="['product:categoryBrand:edit']" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" v-hasPermi="['product:categoryBrand:remove']" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增或修改分类品牌对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="categoryBrandRef" :model="form" :rules="rules"  label-width="80px">
        <el-form-item label="分类" prop="categoryIdList">
          <el-cascader :props="categoryProps" v-model="form.categoryIdList" />
        </el-form-item>
        <el-form-item label="品牌" prop="brandId">
          <el-select v-model="form.brandId" class="m-2" placeholder="选择品牌" size="small">
            <el-option v-for="item in brandList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
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

<script setup name="CategoryBrand">
import {getCategoryBrandList, getCategoryBrand, addCategoryBrand, updateCategoryBrand, deleteCategoryBrand} from "@/api/product/categoryBrand";
import { treeSelect } from "@/api/product/category";
import {getBrandAll} from "@/api/product/brand";
const { proxy } = getCurrentInstance();

// 定义分页列表数据模型
const categoryBrandList = ref([]);
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

const data = reactive({
  // 定义搜索模型
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    brandId: null,
    categoryId: null
  },
  form: {},
  rules: {
    categoryIdList: [{required: true, message: "分类不能为空", trigger: "blur"}],
    brandId: [{required: true, message: "品牌不能为空", trigger: "blur"}]
  }
});
// toRefs 是一个Vue3中提供的Api，可将一个响应式对象转换为普通对象，其中属性变成了对原始对象属性的引用
const {queryParams, form, rules} = toRefs(data);

/* 查询分类品牌列表 */
function getList() {
  loading.value = true;
  getCategoryBrandList(queryParams.value).then(response => {
    categoryBrandList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  })
}


/* 新增按钮操作 */
function handleAdd() {
  resetForm();
  open.value = true;
  title.value = "添加分类品牌";
}

/* 修改按钮操作 */
function handleUpdate(row) {
  resetForm();
  const _id = row.id || ids.value;
  getCategoryBrand(_id).then(response => {
    form.value = response.data
    open.value = true;
    title.value = "修改分类品牌";
  })
}

/* 删除操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除分类品牌编号为"' + _ids + '"的数据项？').then(function () {
    return deleteCategoryBrand(_ids);
  }).then(response => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

/* 表单提交按钮 */
function submitForm() {
  proxy.$refs["categoryBrandRef"].validate(valid => {
    if (valid){
      //系统只需要三级分类id
      form.value.categoryId = form.value.categoryIdList[2];
      if (form.value.id != null) {
        updateCategoryBrand(form.value).then(response => {
          proxy.$modal.msgSuccess("更新成功");
          open.value = false;
          getList();
        })
      } else {
        addCategoryBrand(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        })
      }
    }
  });
}

/* 表单取消按钮 */
function cancel() {
  open.value = false;
  resetForm();
}

/* 重置表单操作 */
function resetForm() {
  form.value = {
    id: null,
    brandId: null,
    categoryId: null,
    categoryIdList: []
  };
  queryCategoryIdList.value = [];
  proxy.resetForm("categoryBrandRef");
}

/* 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
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
// 处理分类按钮事件
const queryCategoryIdList = ref([]);
function handleCategoryChange() {
  if (queryCategoryIdList.value.length == 3) {
    queryParams.value.categoryId = queryCategoryIdList.value[2]
  }
}

/* 选择器---品牌 */
const brandList = ref([])
function getBrandAllList() {
  getBrandAll().then(response => {
    brandList.value = response.data
  })
}
getBrandAllList();

/* 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/* 重置按钮操作 */
function resetQuery() {
  queryCategoryIdList.value = []
  proxy.resetForm("queryRef");
  queryParams.value.categoryId = null
  queryParams.value.brandId = null
  handleQuery();
}

// 执行查询分类品牌列表
onMounted(() => {
  getList();
})
</script>


<style scoped>

</style>