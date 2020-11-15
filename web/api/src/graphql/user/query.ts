import { UserType, UserEnterEvent } from '@/graphql/user/type'
import { Roles } from '@/lib/auth/roles'
import { RequestContext } from '@/loaders/express'
import { UserService } from '@/Service/UserService'
import { GraphQLFieldConfig, GraphQLList, GraphQLString } from 'graphql'
import RoleType from '../role/type'

/**
 * QUERY contains all graphs which retrieves data.
 */

let userService: UserService

/**
 * Retrieves a single user by its id
 */
const User: GraphQLFieldConfig<any, RequestContext, { [id: string]: string }> = {
    type: UserType,
    args: {
        id: { type: GraphQLString },
    },
    resolve: async (root, args, ctx) => {
        if (!ctx.authorizer.isAuthorized()) return
        return await userService.getUser(args.id)
    },
}

/**
 * Retrieves all users in the database
 */
const Users: GraphQLFieldConfig<any, RequestContext> = {
    type: new GraphQLList(UserType),
    resolve: async (root, args, ctx) => {
        if (!ctx.authorizer.isAuthorized()) return []
        return await userService.getAllUsers()
    },
}

/**
 * Retrieves all roles for a user
 */
export const UserRoles: GraphQLFieldConfig<any, RequestContext, { [id: string]: string }> = {
    type: new GraphQLList(RoleType),
    args: {
        id: { type: GraphQLString },
    },
    resolve: async (root, args, ctx) => {
        if (!ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator])) return []
        const id = root.id ? root.id : args.id
        return await userService.getUserRoles(id)
    },
}

/**
 * Retrieves all enter events for a user
 */
export const UserEnterEvents: GraphQLFieldConfig<any, RequestContext, { [id: string]: string }> = {
    type: new GraphQLList(UserEnterEvent),
    resolve: async (root, args, ctx) => {
        if (
            !ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator]) ||
            !ctx.authorizer.isOwner(root.id)
        )
            return []
        return await userService.getUserEnterEvents(root.id)
    },
}

/**
 * Retrieves all enter events for a user
 */
export const UserBlocked: GraphQLFieldConfig<any, RequestContext, { [id: string]: string }> = {
    type: new GraphQLList(UserEnterEvent),
    resolve: async (root, args, ctx) => {
        if (!ctx.authorizer.isAuthorized()) return []
        return await userService.getUserBlocked(root.id)
    },
}

export default (service: UserService) => {
    userService = service
    return { User, Users }
}
