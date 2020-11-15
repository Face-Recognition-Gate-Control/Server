import { UserBlockedType, UserType } from '@/graphql/user/type'
import { GraphQLFieldConfig, GraphQLInt, GraphQLNonNull, GraphQLString } from 'graphql'
import { UserService } from '@/Service/UserService'
import { NewUser } from '@/lib/user/User'
import { Roles } from '@/lib/auth/roles'

/**
 * MUTATION is for updating/creating new data entries
 */

let userSerivce: UserService

/**
 * Creates a new user
 */
var User: GraphQLFieldConfig<any, any, any> = {
    type: UserType,
    args: {
        registration_token: { type: new GraphQLNonNull(GraphQLString) },
        firstname: { type: new GraphQLNonNull(GraphQLString) },
        lastname: { type: new GraphQLNonNull(GraphQLString) },
        email: { type: new GraphQLNonNull(GraphQLString) },
        telephone: { type: new GraphQLNonNull(GraphQLInt) },
        password: { type: new GraphQLNonNull(GraphQLString) },
    },
    resolve: async (root: any, args: NewUser, context: any) => {
        return await userSerivce.createUser({
            registration_token: args.registration_token,
            firstname: args.firstname,
            lastname: args.lastname,
            email: args.email,
            telephone: args.telephone,
            password: args.password,
        })
    },
}

/**
 * Set user to blocked
 */
var SetUserBlock: GraphQLFieldConfig<any, any, any> = {
    type: UserBlockedType,
    resolve: async (root: any, args: NewUser, ctx: any) => {
        if (ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator]) || ctx.authorizer.isOwner()) {
            const user = ctx.authorizer.user
            if (user) {
                return await userSerivce.setUserBlocked(user.id, 'Corona')
            }
        }
    },
}

export default (service: UserService) => {
    userSerivce = service
    return { User, SetUserBlock }
}
