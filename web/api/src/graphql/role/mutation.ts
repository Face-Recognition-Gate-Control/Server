import { GraphQLFieldConfig, GraphQLInt, GraphQLNonNull, GraphQLString } from 'graphql'
import { RoleService } from '@/Service/RoleService'
import RoleType from './type'

/**
 * MUTATION is for updating/creating new data entries
 */

let roleService: RoleService

/**
 * Creates a new role
 */
var Role: GraphQLFieldConfig<any, any, any> = {
    type: RoleType,
    args: {
        role_name: { type: new GraphQLNonNull(GraphQLString) },
    },
    resolve: async (root: any, args: { role_name: string }, context: any) => {
        return await roleService.addNewRole(args.role_name)
    },
}

export default (service: RoleService) => {
    roleService = service
    return { Role }
}
