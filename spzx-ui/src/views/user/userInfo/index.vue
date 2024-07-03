<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关键字">
        <el-input
            style="width: 100%"
            v-model="queryParams.searchValue"
            placeholder="用户名、姓名、手机号码"
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" style="width: 308px">
        <el-date-picker clearable
                        v-model="dateRange"
                        type="daterange"
                        range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 导出、隐藏搜索按钮栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['user:userInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据展示栏 -->
    <el-table v-loading="loading" :data="userInfoList" @selection-change="handleSelectionChange">
      <el-table-column label="用户名" align="left" prop="username" />
      <el-table-column label="昵称" align="left" prop="nickName" />
      <el-table-column label="头像" align="left" prop="avatar" #default="scope">
        <img :src="scope.row.avatar" width="50"/>
      </el-table-column>
      <el-table-column label="性别" align="left" prop="sex" #default="scope">
        {{ scope.row.sex == 1  ? "女" : "男"}}
      </el-table-column>
      <el-table-column label="电话号码" align="left" prop="phone" />
      <el-table-column label="最后一次登录时间" align="left" prop="lastLoginTime" />
      <el-table-column label="状态" align="left" prop="status" #default="scope">
        {{ scope.row.status == 1 ? "正常" : "停用" }}
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleShow(scope.row)" v-hasPermi="['user:userInfo:list']" >详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详情信息 -->
    <el-dialog :title="title" v-model="open" width="60%" append-to-body>
      <el-form ref="userInfoRef" :model="form" label-width="140px">
        <el-divider />
        <span style="margin-bottom: 5px">基本信息</span>
        <el-row>
          <el-col :span="12">
            <el-form-item label="头像" >
              <img :src="form.avatar" width="50"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" >
              {{ form.username }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="昵称" >
              {{ form.nickName }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" >
              {{ form.sex == 1  ? "女" : "男"}}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="电话号码" >
              {{ form.phone }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" >
              {{ form.memo }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最后一次登录IP" >
              {{ form.lastLoginIp }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最后一次登录时间" >
              {{ form.lastLoginTime }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态" >
              {{ form.status == 1 ? "正常" : "停用" }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间" >
              {{ form.createTime }}
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />
        <span style="margin-bottom: 5px">地址信息</span>
        <el-table :data="userAddressList" style="width: 100%">
          <el-table-column label="地址标签" prop="tagName" align="left" width="120" />
          <el-table-column label="是否默认" prop="isDefault" #default="scope" align="left" width="120">
            {{ scope.row.isDefault == 1 ? '是' : '否' }}
          </el-table-column>
          <el-table-column label="详细地址" #default="scope" >
            {{ scope.row.name + '|' + scope.row.phone + '|' + scope.row.fullAddress }}
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" width="250" />
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="UserInfo">
import { listUserInfo, getUserAddress } from "@/api/user/userInfo";

const { proxy } = getCurrentInstance();

const userInfoList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const title = ref('');

const dateRange = ref([]);
const userAddressList = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    searchValue: null,
    startTime: null,
    endTime: null
  }
});

const { queryParams, form } = toRefs(data);

/** 查询会员列表 */
function getList() {
  loading.value = true;
  handleCalendarChange();
  listUserInfo(queryParams.value).then(response => {
    userInfoList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.value.startTime = null;
  queryParams.value.endTime = null;
  queryParams.value.searchValue = null;
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

/* 详情按钮处理 */
function handleShow(row) {
  title.value = "查看会员";
  form.value = row;
  getUserAddress(row.id).then(response => {
    userAddressList.value = response.data
    open.value = true;
  })
}
/* 取消按钮 */
function cancel() {
  open.value = false;
  userAddressList.value= [];
  form.value = {};
}


/** 导出按钮操作 */
function handleExport() {
  proxy.download('user/userInfo/export', {
    ...queryParams.value
  }, `userInfo_${new Date().getTime()}.xlsx`)
}

/* 搜索日期变化处理 */
function handleCalendarChange() {
  if (dateRange.value != null && dateRange.value.length == 2) {
    queryParams.value.startTime = dateRange.value[0];
    queryParams.value.endTime = dateRange.value[1];
  } else {
    queryParams.value.startTime = null;
    queryParams.value.endTime = null;
  }
}

getList();
</script>
