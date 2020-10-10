import express from 'express'
import loader from '@/loaders'

/**
 Responsible for bootstrapping the application
*/
export const bootstrap = async function bootstrap() {
    const server = express()

    await loader({ express: server })

    // await loaders.init();
}
