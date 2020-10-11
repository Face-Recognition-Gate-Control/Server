import express from 'express'
import loader from '@/loaders/index'
import config from '@/config'
import Logger from '@/loaders/logger'

/**
 Responsible for bootstrapping the application
*/
export const bootstrap = async function bootstrap() {
    const server = express()

    await loader({ express: server })

    server.listen(config.port, () => {
        Logger.info(`🗲 Server started on port: ${config.port} 🗲`)
    })
}
