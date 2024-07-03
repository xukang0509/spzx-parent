import request from '@/utils/request';

const baseURL = "/user/userInfo";

// 查询会员列表
export function listUserInfo(query) {
  return request({
    url: `${baseURL}/list`,
    method: 'get',
    params: query
  })
}

// 获取会员地址
export function getUserAddress(userId) {
  return request({
    url: `${baseURL}/getUserAddress/${userId}`,
    method: 'get'
  })
}