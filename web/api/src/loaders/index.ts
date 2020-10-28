import expressLoader from '@/loaders/express'
import { connectToDatabase } from '@/loaders/postgres'
import * as express from 'express'
import logger from '@/loaders/logger'

/**
	Loads all application requirements
*/
export default async ({ express }: { express: express.Application }) => {
    logger.info('STARTING LOADING')

    // Loads express
    await expressLoader({ server: express })
    // Loads database> postgres
    await connectToDatabase()

    logger.info('LOADING COMPLETE')
}
