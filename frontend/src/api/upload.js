import request from '../utils/request'

/**
 * 上传图片文件到服务器
 * @param {File} file - 要上传的图片文件对象
 * @returns {Promise<string>} 上传成功后返回图片的URL地址
 * @example
 * // 使用示例
 * const file = event.target.files[0]
 * const imageUrl = await uploadImage(file)
 * console.log('图片上传成功:', imageUrl)
 */
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
