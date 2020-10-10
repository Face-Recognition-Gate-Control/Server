/**
 * Environment loader
 */
import dotenv from 'dotenv'

// Loads env based on NODE_ENV (production / development)
dotenv.config({ path: `.env.${process.env.NODE_ENV}` })

export default {
    port: process.env.PORT,
}
