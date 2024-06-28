import request from '@/utils/request.js';

const baseURL = `/product/category`;

// 获取商品分类下拉树列表
export function treeSelect(id) {
    return request({
        url: `${baseURL}/treeSelect/${id}`,
        method: 'GET'
    })
}