import { Client } from 'pg'
import config from '@/config'
import logger from '@/loaders/logger'

const sql = new Client({
    host: config.database.host,
    user: config.database.user,
    password: config.database.password,
    database: config.database.database,
})

export default sql

async function connectToDatabase() {
    try {
        await sql.connect()
        logger.info('CONNECTED TO DATABASE')
    } catch (error) {
        logger.error(error, 'DATABASE ERROR')
    }
}

export { connectToDatabase }