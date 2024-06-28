import request from '@/utils/request';

const baseURL = `/product/brand`;

// 查询分类品牌列表
export function getList(query) {
    return request({
        url: `${baseURL}/list`,
        method: 'GET',
        params: query
    })
}
// 查询品牌详情信息
export function getInfo(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'GET'
    })
}
// 新增品牌
export function addBrand(form) {
    return request({
        url: baseURL,
        method: 'POST',
        data: form
    })
}
// 更新品牌
export function updateBrand(form) {
    return request({
        url: baseURL,
        method: 'PUT',
        data: form
    })
}
// 删除品牌
export function deleteBrand(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'DELETE'
    })
}