<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入商品单位名称" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 功能按钮栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" v-hasPermi="['base:productUnit:add']" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" v-hasPermi="['base:productUnit:edit']" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" v-hasPermi="['base:productUnit:remove']" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="listProductUnit" ></right-toolbar>
    </el-row>

    <!-- 数据展示表格 -->
    <el-table v-model="loading" :data="productUnitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="商品单位名称" prop="name" width="200"/>
      <el-table-column label="创建时间" prop="createTime"/>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['base:productUnit:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['base:productUnit:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增或修改商品单位对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="productUnitRef" :rules="rules" :model="form" label-width="80px">
        <el-form-item label="单位名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品单位名称"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分页条组件 -->
    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="listProductUnit"
    />
  </div>
</template>

<script setup name="ProductUnit">
import { getProductUnitList, addProductUnit, getInfoById, updateProductUnit, removeProductUnit } from '@/api/base/productUnit.js';
import RightToolbar from "@/components/RightToolbar/index.vue";
const { proxy } = getCurrentInstance();

// 定义分页列表数据模型
const productUnitList = ref([]);
// 定义列表总记录数模型
const total = ref(0);
// 加载数据时显示的动态控制模型
const loading = ref(true);
// 新增与修改弹出层控制模型
const open = ref(false);
// 新增与修改弹出层标题模型
const title = ref("");
// 定义隐藏搜索控制模型
const showSearch = ref(true);
// 定义批量操作id列表模型
const ids = ref([]);
// 定义单选控制模型
const single = ref(true);
// 定义多选控制模型
const multiple = ref(true);

const data = reactive({
  // 定义搜索模型
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null
  },
  form: {},
  rules: {
    name: [{ required: true, message: "商品单位名称不能为空", trigger: "blur"}]
  }
});
// toRefs 是一个Vue3中提供的Api，可将一个响应式对象转换为普通对象，其中属性变成了对原始对象属性的引用
const {queryParams, form, rules} = toRefs(data);

/* 查询商品单位列表 */
function listProductUnit() {
  loading.value = true;
  getProductUnitList(queryParams.value).then(response => {
    productUnitList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  })
}

/* 新增按钮操作 */
function handleAdd() {
  resetForm();
  open.value = true;
  title.value = "添加商品单位";
}

/* 修改按钮操作 */
function handleUpdate(row) {
  resetForm();
  const _id = row.id || ids.value;
  getInfoById(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改商品单位";
  })
}

/* 新增修改提交按钮操作 */
function submitForm() {
  proxy.$refs["productUnitRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateProductUnit(form.value).then(() => {
          proxy.$modal.msgSuccess("更新成功");
          open.value = false;
          listProductUnit();
        })
      } else {
        addProductUnit(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          listProductUnit();
        })
      }
    }
  });
}

/* 删除操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除商品单位编号为"' + _ids + '"的数据项？').then(function () {
    return removeProductUnit(_ids);
  }).then(() => {
    listProductUnit();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/* 多选商品单位处理 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/* 搜索按钮操作 */
function handleQuery() {
  listProductUnit();
}

/* 搜索重置操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/* 表单取消按钮 */
function cancel() {
  open.value = false;
  resetForm();
}

/* 表单重置 */
function resetForm() {
  form.value = {
    id: null,
    name: null
  };
  proxy.resetForm("productUnitRef");
}

onMounted(() => {
  listProductUnit();
})
</script>

<style scoped>
</style>