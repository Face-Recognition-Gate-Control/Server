import RoleType from '@/graphql/role/type'
import { RoleService } from '@/Service/RoleService'
import { GraphQLFieldConfig, GraphQLList, GraphQLString } from 'graphql'

/**
 * QUERY contains all graphs which retrieves data.
 */

let roleService: RoleService

/**
 * Retrieves a single role by its name
 */
const Role: GraphQLFieldConfig<any, any, { [name: string]: string }> = {
    type: RoleType,
    args: {
        name: { type: GraphQLString },
    },
    resolve: async (root, args) => {
        return await roleService.getRole(args.name)
    },
}

/**
 * Retrieves all roles in the database
 */
const Roles: GraphQLFieldConfig<any, any> = {
    type: new GraphQLList(RoleType),
    resolve: async () => {
        return await roleService.getAllRoles()
    },
}

export default (service: RoleService) => {
    roleService = service
    return { Role, Roles }
}
