import { Client } from 'pg'
import config from '@/config'
const sql = new Client({
    host: config.database.host,
    user: config.database.user,
    password: config.database.password,
    database: config.database.database,
})
export default sql
