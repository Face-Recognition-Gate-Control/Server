/**
 * Environment loader
 */
import dotenv from 'dotenv'

// Loads env based on NODE_ENV (production / development)
dotenv.config({ path: `.env.${process.env.NODE_ENV}` })

export default {
    port: process.env.PORT,
    api_prefix: '/api',
    database: {
        database: process.env.DATABASE_DATABASE,
        host: process.env.DATABASE_HOST,
        port: process.env.DATABASE_PORT,
        user: process.env.DATABASE_USER,
        password: process.env.DATABASE_PASSWORD,
    },
}
