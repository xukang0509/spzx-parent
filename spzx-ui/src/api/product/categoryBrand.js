import request from '@/utils/request';

const baseURL = `/product/categoryBrand`;

// 查询分类品牌列表
export function getCategoryBrandList(query) {
    return request({
        url: `${baseURL}/list`,
        method: 'GET',
        params: query
    })
}
// 查询分类品牌信息
export function getCategoryBrand(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'GET'
    })
}
// 新增分类品牌
export function addCategoryBrand(form) {
    return request({
        url: baseURL,
        method: 'POST',
        data: form
    })
}
// 更新分类品牌
export function updateCategoryBrand(form) {
    return request({
        url: baseURL,
        method: 'PUT',
        data: form
    })
}
// 删除分类品牌
export function deleteCategoryBrand(id) {
    return request({
        url: `${baseURL}/${id}`,
        method: 'DELETE'
    })
}
// 根据分类ID获取品牌列表
export function getBrandListByCategoryId(categoryId) {
    return request({
        url: `${baseURL}/brandList/${categoryId}`,
        method: 'GET'
    })
}