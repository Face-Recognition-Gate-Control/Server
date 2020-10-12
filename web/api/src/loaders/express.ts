/**
 * Loads and setup express
 */
import * as express from 'express'
import cors from 'cors'
import { generateSchema } from '@/graphql'
import { graphqlHTTP } from 'express-graphql'

export default async ({ server }: { server: express.Application }) => {
    server.get('/status', (req, res) => {
        res.status(200).end()
    })

    /* Parses request.body to JSON */
    server.use(express.json())

    /* Enable cross origin resources */
    server.use(cors())

    /**
     * Setup graphql root
     */
    server.use(
        '/graphql',
        graphqlHTTP(async (req) => ({
            schema: generateSchema(),
            /**
             * Enable / disable Webinterface
             */
            graphiql: true,
            /**
             * Context will be injected into all requests, and is accessible
             * through context parameter.
             */
            context: {
                /**
                 * HTTP - Express Request
                 */
                request: req,
                protected: false,
            },
        }))
    )

    return server
}
