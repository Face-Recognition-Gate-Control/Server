/**
 * Environment loader
 */
import dotenv from 'dotenv'
let config = () => {
    if (process.env.NODE_ENV == 'development') {
        // Loads env based on NODE_ENV ( development)
        dotenv.config({ path: `.env.${process.env.NODE_ENV}` })

        return {
            port: process.env.PORT,
            jwt_secret: process.env.JWT_SECRET,
            database: {
                database: process.env.DATABASE_DATABASE,
                host: process.env.DATABASE_HOST,
                port: process.env.DATABASE_PORT,
                user: process.env.DATABASE_USER,
                password: process.env.DATABASE_PASSWORD,
            },
        }
    } else {
        return {
            port: process.env.API_PORT,
            jwt_secret: process.env.JWT_SECRET,
            database: {
                database: process.env.USER_DB_NAME,
                host: process.env.POSTGRES_IP,
                port: process.env.POSTGRES_PORT,
                user: process.env.USER_USERNAME,
                password: process.env.USER_PASSWORD,
            },
        }
    }
}

export default config()
