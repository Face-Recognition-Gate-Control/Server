export const isEmpty = (value: string) => {
  return value ? false : true
}

export const isEmail = (value: string) => {
  return /^[a-zA-Z0-9.-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9]+)*$/.test(value)
}

export const isEqual = (value: string, value2: string) => {
  return value === value2
}

export const range = (value: string, min?: number, max?: number) => {
  let valid = true
  if (min) {
    if (value.length < min) valid = false
  }
  if (max) {
    if (value.length > max) valid = false
  }
  return valid
}
