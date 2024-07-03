<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="订单号" prop="orderNo" >
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleQuery"/>
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select v-model="queryParams.orderStatus" placeholder="订单状态"  class="m-2" style="width: 100%">
          <el-option v-for="item in orderStatusList" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
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
      <el-form-item label="收货人姓名" prop="receiverName">
        <el-input
          v-model="queryParams.receiverName"
          placeholder="请输入收货人姓名"
          clearable
          @keyup.enter="handleQuery"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="收货人电话" prop="receiverPhone">
        <el-input
          v-model="queryParams.receiverPhone"
          placeholder="请输入收货人电话"
          clearable
          @keyup.enter="handleQuery"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 功能栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['order:orderInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据展示栏 -->
    <el-table v-loading="loading" :data="orderInfoList">
      <el-table-column label="订单号" prop="orderNo"/>
      <el-table-column label="订单总额" prop="totalAmount" width="80"/>
      <el-table-column label="订单状态" prop="orderStatus" #default="scope" width="80">
        {{
          scope.row.orderStatus == 0 ? "待支付" :
            (scope.row.orderStatus == 1 ? "待发货" :
              (scope.row.orderStatus == 2 ? "已发货" : (scope.row.orderStatus == 3 ? "完成" : "已取消")))
        }}
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime"  width="180"/>
      <el-table-column label="支付时间" prop="paymentTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.paymentTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="收货人姓名" prop="receiverName" width="120" />
      <el-table-column label="收货人电话" prop="receiverPhone" width="140"/>
      <el-table-column label="详细地址" prop="receiverAddress" width="270" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleShow(scope.row)" v-hasPermi="['order:orderInfo:list']">详情</el-button>
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

    <!-- 详情 -->
    <el-dialog :title="title" v-model="open" width="60%" append-to-body>
      <el-form ref="orderInfoRef" :model="form" label-width="140px">
        <el-divider/>
        <span style="margin-bottom: 5px;font-weight:bold;">订单基本信息</span>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单号">{{form.orderNo}}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单总额">{{form.totalAmount}}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优惠券">{{form.couponAmount}}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价金额">{{form.originalTotalAmount}}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="运费">{{form.feightFee}}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式">支付宝</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单状态">
              {{
                form.orderStatus == 0 ? "待支付" :
                    (form.orderStatus == 1 ? "待发货" :
                        (form.orderStatus == 2 ? "已发货" : (form.orderStatus == 3 ? "完成" : "已取消")))
              }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">{{form.createTime}}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="支付时间">{{form.paymentTime}}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单备注">{{ form.remark }}</el-form-item>
          </el-col>
        </el-row>

        <el-divider/>
        <span style="margin-bottom: 5px;font-weight:bold;">收货人信息</span>
        <el-row>
          <el-col :span="12">
            <el-form-item label="收货人姓名">{{form.receiverName}}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收货人电话">{{ form.receiverPhone }}</el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="地址标签">{{form.receiverTagName}}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收货详细地址">{{ form.receiverAddress }}</el-form-item>
          </el-col>
        </el-row>

        <el-divider/>
        <span style="margin-bottom: 5px;font-weight:bold;">订单明细信息</span>
        <el-table :data="form.orderItemList" style="width: 100%">
          <el-table-column label="SKU名称" prop="skuName" width="300" />
          <el-table-column label="图片" #default="scope">
            <img :src="scope.row.thumbImg" width="60"/>
          </el-table-column>
          <el-table-column label="SKU价格" prop="skuPrice" />
          <el-table-column label="购买数量" prop="skuNum" />
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

<script setup name="OrderInfo">
import { listOrderInfo, getOrderInfo } from "@/api/order/orderInfo";

const { proxy } = getCurrentInstance();

const orderInfoList = ref([]);
const total = ref(0);
const loading = ref(true);
const showSearch = ref(true);
const open = ref(false);
const title = ref("");

const dateRange = ref([]);
const orderStatusList = [
  {id: 0, name: "待付款"},
  {id: 1, name: "待发货"},
  {id: 2, name: "已发货"},
  {id: 3, name: "完成"},
  {id: -1, name: "已取消"}
]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: '',
    receiverName: '',
    receiverPhone: '',
    orderStatus: '',
    startTime: '',
    endTime: ''
  }
});

const { queryParams, form } = toRefs(data);

/** 查询订单列表 */
function getList() {
  loading.value = true;
  handleCalendarChange();
  listOrderInfo(queryParams.value).then(response => {
    orderInfoList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 详情按钮操作 */
function handleShow(row) {
  title.value = "订单详情";
  getOrderInfo(row.id).then(response => {
    form.value = response.data;
    open.value = true;
  })
}

/** 订单详情取消按钮 */
function cancel() {
  open.value = false;
  form.value = {};
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.value = {
    orderNo: '',
    receiverName: '',
    receiverPhone: '',
    orderStatus: '',
    startTime: '',
    endTime: ''
  };
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('order/orderInfo/export', {
    ...queryParams.value
  }, `orderInfo_${new Date().getTime()}.xlsx`)
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
