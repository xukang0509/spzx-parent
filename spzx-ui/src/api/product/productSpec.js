import request from '@/utils/request';

const baseURL = `/product/productSpec`;

// 查询商品规格列表
export function getProductSpecList(query) {
    return request({
        url: `${baseURL}/list`,
        method: 'GET',
        params: query
    })
}
// 查询商品规格信息
export function getProductSpec(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'GET'
    })
}
// 新增商品规格
export function addProductSpec(form) {
    return request({
        url: baseURL,
        method: 'POST',
        data: form
    })
}
// 更新商品规格
export function updateProductSpec(form) {
    return request({
        url: baseURL,
        method: 'PUT',
        data: form
    })
}
// 删除商品规格
export function deleteProductSpec(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'DELETE'
    })
}