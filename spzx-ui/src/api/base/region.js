import request from '@/utils/request.js'
const baseURL = "/user/region";

// 地区级联查询
export function treeRegionSelect(parentCode) {
  return request({
    url: `${baseURL}/treeSelect/${parentCode}`,
    method: 'get'
  })
}