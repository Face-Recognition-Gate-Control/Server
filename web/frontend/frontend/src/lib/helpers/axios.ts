import axios from 'axios'

const gqlAxios = axios.create({
  baseURL: 'http://192.168.1.100:8080/graphql',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  }
})

export { gqlAxios }
