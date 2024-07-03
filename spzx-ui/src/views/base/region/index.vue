<template>
  <div class="app-container">
    <el-tree :props="props" :load="loadNode" lazy />
  </div>
</template>

<script setup name="Region">
import {treeRegionSelect} from "@/api/base/region.js";
const {proxy} = getCurrentInstance();

const props = {
  label: 'name',
  children: 'zones',
  isLeaf: 'leaf'
}

const list = ref([]);
const parentCode = ref('0');

const loadNode = async (node, resolve) => {
  if (node.data.code) {
    parentCode.value = node.data.code;
  }
  const { data } = await treeRegionSelect(parentCode.value);
  data.forEach(item => {
    item.leaf = (item.level == 3 ? true : false);
  });
  resolve(data);
}
</script>
