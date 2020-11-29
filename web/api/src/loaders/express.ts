/**
 * Loads and setup express
 */
import * as express from 'express'
import cors from 'cors'
import { generateSchema } from '@/graphql'
import { graphqlHTTP } from 'express-graphql'
import { UserRequestAuthentication } from '@/lib/auth/UserRequestAuthentication'
import { IncomingMessage } from 'http'
import { Response } from 'express'
import { Authorizer } from '@/lib/auth/Authorizer'

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
        graphqlHTTP(async (request, response) => ({
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
                request,
                response,
                authorizer: await UserRequestAuthentication(request.headers.authorization),
                // authorizer: await UserRequestAuthentication(
                //     'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiIxIiwiaWF0IjoxNjAzMDI5NTE5fQ.Cd1UIarugfWBb3Oj0nng3n8uG_ubIMKp0WuF02hv904'
                // ),
            },
        }))
    )

    return server
}

export type RequestContext = {
    request: IncomingMessage
    response: Response
    authorizer: Authorizer
}
