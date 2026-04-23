import { describe, expect, it } from 'vitest'
import { normalizeDisplayText, normalizeTextFields } from './text'

describe('text display normalization', () => {
  it('repairs UTF-8 Chinese mojibake produced through Windows-1252/Latin-1 decoding', () => {
    expect(normalizeDisplayText('ç®¡ç†å‘˜')).toBe('管理员')
    expect(normalizeDisplayText('æŠ€æœ¯éƒ¨')).toBe('技术部')
  })

  it('keeps already valid text unchanged', () => {
    expect(normalizeDisplayText('管理员')).toBe('管理员')
    expect(normalizeDisplayText('admin')).toBe('admin')
  })

  it('normalizes nested response payloads', () => {
    expect(normalizeTextFields({
      name: 'ç®¡ç†å‘˜',
      role: 'admin',
      departments: ['æŠ€æœ¯éƒ¨']
    })).toEqual({
      name: '管理员',
      role: 'admin',
      departments: ['技术部']
    })
  })
})
