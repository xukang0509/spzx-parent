import request from '@/utils/request';

const baseURL = `/product/product`;

// 查询商品列表
export function getProductList(query) {
    return request({
        url: `${baseURL}/list`,
        method: 'GET',
        params: query
    })
}
// 查询商品信息
export function getProduct(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'GET'
    })
}
// 新增商品
export function addProduct(form) {
    return request({
        url: baseURL,
        method: 'POST',
        data: form
    })
}
// 更新商品
export function updateProduct(form) {
    return request({
        url: baseURL,
        method: 'PUT',
        data: form
    })
}
// 删除商品
export function deleteProduct(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'DELETE'
    })
}
// 商品审核
export function updateAuditStatus(id, auditStatus) {
    return request({
        url: `${baseURL}/updateAuditStatus/${id}/${auditStatus}`,
        method: 'GET'
    })
}
// 更新上下架信息
export function updateStatus(id, status) {
    return request({
        url: `${baseURL}/updateStatus/${id}/${status}`,
        method: 'GET'
    })
}