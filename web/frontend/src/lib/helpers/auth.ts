// inspirations from https://chrismroberts.com/2019/01/03/authentication-and-protected-routes-in-vuejs/
import jwtdecoder from 'jwt-decode'
import { gqlAxios } from './axios'

const tokenKey = 'jwt'

export function setToken(token: string) {
  window.sessionStorage.setItem(tokenKey, token)
  setAxiosHeader(token)
}

function setAxiosHeader(token: string) {
  gqlAxios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

export function getToken() {
  return window.sessionStorage.getItem(tokenKey)
}

export function isLoggedIn() {
  const token = getToken()

  if (token) {
    setAxiosHeader(token)
    return !isTokenExpired(token)
  } else {
    return false
  }
}

export function logout() {
  clearToken()
}

function clearToken() {
  gqlAxios.defaults.headers.common['Authorization'] = ``
  window.sessionStorage.removeItem(tokenKey)
}

function isTokenExpired(token: string) {
  let expirationDate = getTokenExpirationDate(token)
  if (expirationDate) {
    return expirationDate < new Date()
  }
  return false
}

function getTokenExpirationDate(token: string) {
  let decoded: any = jwtdecoder(token)

  if (!decoded.exp) {
    return null
  }

  let date = new Date(0)
  date.setUTCSeconds(decoded.exp)

  return date
}
