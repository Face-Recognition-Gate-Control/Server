import { GraphQLFieldConfig, GraphQLNonNull, GraphQLString } from 'graphql'
import { AuthenticationService } from '@/Service/AuthenticationService'
import { AuthenticationType } from './type'
import { Credentials } from '@/lib/auth/Credentials'
import { RequestContext } from '@/loaders/express'

let authService: AuthenticationService

/**
 * Authenticates a user
 */
var Auth: GraphQLFieldConfig<any, RequestContext, any> = {
    type: AuthenticationType,
    args: {
        email: { type: new GraphQLNonNull(GraphQLString) },
        password: { type: new GraphQLNonNull(GraphQLString) },
    },
    resolve: async (root: any, args: Credentials, ctx) => {
        const auth = await authService.authenticateUser({ ...args })
        if (auth?.user) {
            ctx.authorizer.user = auth.user
        }
        return auth
    },
}

export default (service: AuthenticationService) => {
    authService = service
    return { Auth }
}
