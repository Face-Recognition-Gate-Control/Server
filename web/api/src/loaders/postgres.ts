import { Client } from 'pg'
import config from '@/config'
import logger from '@/loaders/logger'

/**
 * Instantiates a new postgres Client.
 * This will not connect on creation and reqires a connection call.
 */
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
        return sql
    } catch (error) {
        logger.error(error, 'DATABASE ERROR')
    }
}

export { connectToDatabase }
