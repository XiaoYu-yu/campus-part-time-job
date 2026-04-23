const MOJIBAKE_PATTERN = /[ÃÂ€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ]|[\u0080-\u009f]|[äåæçèé][\u0080-\u00ff€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ]?/i
const CJK_PATTERN = /[\u3400-\u9fff]/
const MOJIBAKE_SCORE_PATTERN = /[ÃÂ€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ]|[\u0080-\u009f]|[äåæçèé][\u0080-\u00ff€‚ƒ„…†‡ˆ‰Š‹ŒŽ‘’“”•–—˜™š›œžŸ]?|�/gi

const WINDOWS_1252_BYTE_MAP = new Map([
  [0x20ac, 0x80],
  [0x201a, 0x82],
  [0x0192, 0x83],
  [0x201e, 0x84],
  [0x2026, 0x85],
  [0x2020, 0x86],
  [0x2021, 0x87],
  [0x02c6, 0x88],
  [0x2030, 0x89],
  [0x0160, 0x8a],
  [0x2039, 0x8b],
  [0x0152, 0x8c],
  [0x017d, 0x8e],
  [0x2018, 0x91],
  [0x2019, 0x92],
  [0x201c, 0x93],
  [0x201d, 0x94],
  [0x2022, 0x95],
  [0x2013, 0x96],
  [0x2014, 0x97],
  [0x02dc, 0x98],
  [0x2122, 0x99],
  [0x0161, 0x9a],
  [0x203a, 0x9b],
  [0x0153, 0x9c],
  [0x017e, 0x9e],
  [0x0178, 0x9f]
])

const textDecoder = typeof TextDecoder !== 'undefined'
  ? new TextDecoder('utf-8', { fatal: true })
  : null

const countMatches = (value, pattern) => {
  const matches = value.match(pattern)
  return matches ? matches.length : 0
}

const countCjk = (value) => {
  const matches = value.match(/[\u3400-\u9fff]/g)
  return matches ? matches.length : 0
}

/**
 * 修复 UTF-8 中文被按 Latin-1 误解码后的 mojibake。
 * 只在解码结果明显更像中文时替换，避免误伤普通英文、数字和状态枚举。
 */
export const normalizeDisplayText = (value) => {
  if (typeof value !== 'string' || !MOJIBAKE_PATTERN.test(value) || !textDecoder) {
    return value
  }

  const bytes = []
  for (const char of value) {
    const code = char.charCodeAt(0)
    if (WINDOWS_1252_BYTE_MAP.has(code)) {
      bytes.push(WINDOWS_1252_BYTE_MAP.get(code))
    } else if (code <= 255) {
      bytes.push(code)
    } else {
      return value
    }
  }

  try {
    const decoded = textDecoder.decode(new Uint8Array(bytes))
    const sourceScore = countMatches(value, MOJIBAKE_SCORE_PATTERN)
    const decodedScore = countMatches(decoded, MOJIBAKE_SCORE_PATTERN)

    if (CJK_PATTERN.test(decoded) && countCjk(decoded) > countCjk(value) && decodedScore < sourceScore) {
      return decoded
    }
  } catch {
    return value
  }

  return value
}

export const normalizeTextFields = (payload) => {
  if (typeof payload === 'string') {
    return normalizeDisplayText(payload)
  }

  if (Array.isArray(payload)) {
    return payload.map(item => normalizeTextFields(item))
  }

  if (payload && typeof payload === 'object') {
    return Object.fromEntries(
      Object.entries(payload).map(([key, value]) => [key, normalizeTextFields(value)])
    )
  }

  return payload
}
