import request from '@/utils/request.js';

const baseURL = `/product/productUnit`;

// 查询分类品牌列表
export function getProductUnitList(query) {
    return request({
        url: `${baseURL}/list`,
        method: 'GET',
        params: query
    })
}

// 获取商品单位详情信息
export function getInfoById(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'GET'
    })
}

// 新增商品单位
export function addProductUnit(data) {
    return request({
        url: baseURL,
        method: 'POST',
        data: data
    })
}

// 更新商品单位
export function updateProductUnit(data) {
    return request({
        url: baseURL,
        method: 'PUT',
        data: data
    })
}

// 删除商品单位
export function removeProductUnit(ids) {
    return request({
        url: `${baseURL}/${ids}`,
        method: 'DELETE'
    })
}

// 获取全部商品单元
export function getProductUnitAll() {
    return request({
        url: `${baseURL}/getUnitAll`,
        method: 'GET'
    })
}