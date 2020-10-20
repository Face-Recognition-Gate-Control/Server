import { GraphQLFieldConfig, GraphQLInt, GraphQLNonNull, GraphQLString } from 'graphql'
import { RoleService } from '@/Service/RoleService'
import RoleType from './type'
import { RequestContext } from '@/loaders/express'
import { Roles } from '@/lib/auth/roles'

/**
 * MUTATION is for updating/creating new data entries
 */

let roleService: RoleService

/**
 * Creates a new role
 */
var Role: GraphQLFieldConfig<any, RequestContext, any> = {
    type: RoleType,
    args: {
        role_name: { type: new GraphQLNonNull(GraphQLString) },
    },
    resolve: async (root: any, args: { role_name: string }, ctx) => {
        if (!ctx.authorizer.hasRole([Roles.Admin])) return
        return await roleService.addNewRole(args.role_name)
    },
}

export default (service: RoleService) => {
    roleService = service
    return { Role }
}
