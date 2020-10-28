import RoleType from '@/graphql/role/type'
import { Roles as UserRoles } from '@/lib/auth/roles'
import { RequestContext } from '@/loaders/express'
import { RoleService } from '@/Service/RoleService'
import { GraphQLFieldConfig, GraphQLList, GraphQLString } from 'graphql'

/**
 * QUERY contains all graphs which retrieves data.
 */

let roleService: RoleService

/**
 * Retrieves a single role by its name
 */
const Role: GraphQLFieldConfig<any, RequestContext, { [name: string]: string }> = {
    type: RoleType,
    args: {
        name: { type: GraphQLString },
    },
    resolve: async (root, args, ctx) => {
        if (!ctx.authorizer.hasRole([UserRoles.Admin, UserRoles.Moderator])) return
        return await roleService.getRole(args.name)
    },
}

/**
 * Retrieves all roles in the database
 */
const Roles: GraphQLFieldConfig<any, RequestContext> = {
    type: new GraphQLList(RoleType),
    resolve: async (root, args, ctx) => {
        if (!ctx.authorizer.hasRole([UserRoles.Admin, UserRoles.Moderator])) return
        return await roleService.getAllRoles()
    },
}

export default (service: RoleService) => {
    roleService = service
    return { Role, Roles }
}
